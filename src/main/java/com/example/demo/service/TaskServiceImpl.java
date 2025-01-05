package com.example.demo.service;

import com.example.demo.converter.TaskConverter;
import com.example.demo.dto.Task;
import com.example.demo.entity.TaskVersion;
import com.example.demo.entity.TaskVersionKey;
import com.example.demo.entity.Version;
import com.example.demo.properties.TaskListProperties;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.TaskVersionRepository;
import com.example.demo.repository.VersionRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@NoArgsConstructor
public class TaskServiceImpl implements TaskService{
    private final static Random random = new Random();
    private TaskRepository taskRepository;
    private TaskConverter taskConverter;
    private TaskListProperties taskListProperties;
    private VersionRepository versionRepository;
    private TaskVersionRepository taskVersionRepository;

    @Autowired
    public TaskServiceImpl(
        TaskRepository repository,
        TaskConverter converter,
        TaskListProperties taskListProperties,
        VersionRepository versionRepository,
        TaskVersionRepository taskVersionRepository
    ) {
        this.taskConverter = converter;
        this.taskRepository = repository;
        this.taskListProperties = taskListProperties;
        this.versionRepository = versionRepository;
        this.taskVersionRepository = taskVersionRepository;
    }

    @Transactional
    public List<Task> getTasks(){
        List<Task> result = new ArrayList<>();
        List<com.example.demo.entity.Task> allTasks = StreamSupport.stream(
                taskRepository.findAll().spliterator(),
                false
            )
            .toList();
        List<com.example.demo.entity.Task> randomTasks = getRandomTasks(allTasks);
        result.addAll(
            randomTasks.stream()
                .map(taskConverter::convert)
                .toList()
        );
        saveTasksVersionIfNeeded(result, randomTasks);
        return result;
    }

    public Task getTask(Long id){
        return taskRepository.findById(id)
            .map(taskConverter::convert)
            .orElse(null);
    }

    private List<com.example.demo.entity.Task> extractNLevelTasks(
        List<com.example.demo.entity.Task> allTasks,
        Supplier<Long> getter,
        int level
    ){
        var levelTasks = allTasks.stream()
            .filter(task -> task.getLevel() == level)
            .collect(Collectors.toSet());
        var countToSkip = Math.min(random.nextInt(0, levelTasks.size()), levelTasks.size() - getter.get());
        return levelTasks.stream()
            .skip(countToSkip)
            .limit(getter.get())
            .toList();
    }
    private List<com.example.demo.entity.Task> getRandomTasks(List<com.example.demo.entity.Task> allTasks){
        List<com.example.demo.entity.Task> result = new ArrayList<>();
        result.addAll(extractNLevelTasks(allTasks, () -> taskListProperties.getTaskList().getFirstLevel(), 1));
        result.addAll(extractNLevelTasks(allTasks, () -> taskListProperties.getTaskList().getSecondLevel(), 2));
        result.addAll(extractNLevelTasks(allTasks, () -> taskListProperties.getTaskList().getThirdLevel(), 3));
        return result;
    }

    private void saveTasksVersionIfNeeded(
        List<Task> allTasks,
        List<com.example.demo.entity.Task> randomTasks
    ){
        String hashcode = allTasks.stream()
            .map(task -> String.valueOf(task.getId()))
            .collect(Collectors.joining(""));
        Optional<Version> versionOptional = versionRepository.findVersionByHashcode(hashcode);
        if (versionOptional.isPresent()) {
            return;
        }
        Version version = versionRepository.save(
            Version.builder()
                .hashcode(hashcode)
                .build()
        );
        randomTasks.stream()
            .map(task -> TaskVersion.builder()
                .id(
                    TaskVersionKey.builder()
                        .taskId(task.getId())
                        .versionId(version.getId())
                        .build()
                )
                .version(version)
                .task(task)
                .build())
            .forEach(taskVersionRepository::save);
    }
}
