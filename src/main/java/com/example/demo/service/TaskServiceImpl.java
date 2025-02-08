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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    @Override
    public List<Task> getTasks(String hashcode) {
        List<TaskVersion> tasks = taskVersionRepository.findTaskVersionByVersionHashcode(hashcode);
        return tasks.stream()
            .sorted(Comparator.comparing(TaskVersion::getIndex))
            .map(TaskVersion::getTask)
            .map(taskConverter::convert)
            .toList();
    }

    @Transactional
    public String generateRandomVersion(){
        List<com.example.demo.entity.Task> allTasks = StreamSupport.stream(
                taskRepository.findAll().spliterator(),
                false
            )
            .toList();
        List<com.example.demo.entity.Task> randomTasks = getRandomTasks(allTasks);
        return saveTasksVersionIfNeeded(randomTasks);
    }

    public Task getTask(Long id){
        return taskRepository.findById(id)
            .map(taskConverter::convert)
            .orElse(null);
    }

    private List<com.example.demo.entity.Task> extractNLevelTasks(
        List<com.example.demo.entity.Task> allTasks,
        Long taskCountToExtract,
        int level
    ){
        var appropriateLevelTasks = allTasks.stream()
            .filter(task -> task.getLevel() == level)
            .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(appropriateLevelTasks);
        return appropriateLevelTasks.stream()
            .limit(taskCountToExtract)
            .sorted(Comparator.comparing(com.example.demo.entity.Task::getId))
            .toList();
    }
    private List<com.example.demo.entity.Task> getRandomTasks(List<com.example.demo.entity.Task> allTasks){
        List<com.example.demo.entity.Task> result = new ArrayList<>();
        result.addAll(extractNLevelTasks(allTasks, taskListProperties.getTaskList().getFirstLevel(), 1));
        result.addAll(extractNLevelTasks(allTasks, taskListProperties.getTaskList().getSecondLevel(), 2));
        result.addAll(extractNLevelTasks(allTasks, taskListProperties.getTaskList().getThirdLevel(), 3));
        return result;
    }

    private String saveTasksVersionIfNeeded(
        List<com.example.demo.entity.Task> randomTasks
    ){
        String hashcode = String.valueOf(randomTasks.stream()
                .map(com.example.demo.entity.Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")).hashCode());
        Optional<Version> versionOptional = versionRepository.findVersionByHashcode(hashcode);
        if (versionOptional.isPresent()) {
            return hashcode;
        }
        Version version = versionRepository.save(
            Version.builder()
                .hashcode(hashcode)
                .build()
        );
        AtomicReference<Long> index = new AtomicReference<>(0L);
        randomTasks.stream()
            .map(task ->
                TaskVersion.builder()
                    .id(
                        TaskVersionKey.builder()
                            .taskId(task.getId())
                            .versionId(version.getId())
                            .build()
                    )
                    .version(version)
                    .task(task)
                    .index(index.getAndSet(index.get() + 1))
                    .build()
            )
            .forEach(taskVersionRepository::save);
        return hashcode;
    }
}
