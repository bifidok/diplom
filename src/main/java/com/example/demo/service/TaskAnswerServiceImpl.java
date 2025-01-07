package com.example.demo.service;

import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAnswerResultTask;
import com.example.demo.entity.TaskAnswerResultTaskKey;
import com.example.demo.repository.TaskAnswerResultRepository;
import com.example.demo.repository.TaskAnswerResultTaskRepository;
import com.example.demo.repository.TaskRepository;
import org.apache.el.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskAnswerServiceImpl implements TaskAnswerService {
    private final static Long TASK_LEVEL_TO_COMPILE = 3l;
    private TaskRepository taskRepository;
    private TaskAnswerResultRepository taskAnswerResultRepository;
    private TaskAnswerResultTaskRepository taskAnswerResultTaskRepository;
    private JdoodleService jdoodleService;

    @Autowired
    public TaskAnswerServiceImpl(
        TaskRepository taskRepository,
        TaskAnswerResultRepository taskAnswerResultRepository,
        TaskAnswerResultTaskRepository taskAnswerResultTaskRepository,
        JdoodleService jdoodleService
    ) {
        this.taskRepository = taskRepository;
        this.taskAnswerResultRepository = taskAnswerResultRepository;
        this.taskAnswerResultTaskRepository = taskAnswerResultTaskRepository;
        this.jdoodleService = jdoodleService;
    }

    @Override
    public Long getScoreByResult(Long resultId) {
        return taskAnswerResultRepository.findById(resultId)
            .map(com.example.demo.entity.TaskAnswerResult::getScore)
            .orElseThrow();
    }

    @Override
    @Transactional
    public TaskAnswerResult processAnswers(List<TaskAnswer> answers) {
        Map<Long, Task> taskIdToTask = StreamSupport.stream(
            taskRepository.findAllById(
                answers.stream()
                    .map(TaskAnswer::getTaskId)
                    .collect(Collectors.toSet()
            )
        ).spliterator(), false)
        .collect(Collectors.toMap(Task::getId, Function.identity()));

        List<Task> correctAnsweredTasks = new ArrayList<>();
        Map<TaskAnswer, Task> tasksToCompile = new HashMap<>();

        answers.stream()
            .filter(taskAnswer -> {
                var task = taskIdToTask.get(taskAnswer.getTaskId());
                if(task.getLevel() == TASK_LEVEL_TO_COMPILE) {
                    tasksToCompile.put(taskAnswer, task);
                    return false;
                }
                boolean correctAnswered = task.getAnswers().stream()
                    .map(com.example.demo.entity.TaskAnswer::getAnswer)
                    .collect(Collectors.toSet()).contains(taskAnswer.getAnswer());
                if(correctAnswered) {
                    correctAnsweredTasks.add(task);
                }
                return correctAnswered;
            })
            .collect(Collectors.toSet());

        var result = taskAnswerResultRepository.save(com.example.demo.entity.TaskAnswerResult.builder()
                .score(
                    (long) correctAnsweredTasks.stream()
                        .mapToInt(Task::getLevel)
                        .sum()
                )
                .build());
        taskIdToTask.values().stream()
                .map(task -> TaskAnswerResultTask.builder()
                    .id(TaskAnswerResultTaskKey.builder()
                        .taskAnswerResultId(result.getId())
                        .taskId(task.getId())
                        .build())
                    .taskAnswerResult(result)
                    .task(task)
                    .build()
                ).forEach(taskAnswerResultTaskRepository::save);

        tasksToCompile.keySet().stream()
            .forEach(answer -> executeCode(answer.getAnswer()));
        return TaskAnswerResult.builder()
            .resultUrl("http://localhost:8080/answer/" + result.getId())
            .build();
    }

    private String executeCode(String code) {
        return jdoodleService.executeCode(code);
    }
}
