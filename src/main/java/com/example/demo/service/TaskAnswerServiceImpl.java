package com.example.demo.service;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAnswerResultTask;
import com.example.demo.entity.TaskAnswerResultTaskKey;
import com.example.demo.enums.Language;
import com.example.demo.repository.TaskAnswerResultRepository;
import com.example.demo.repository.TaskAnswerResultTaskRepository;
import com.example.demo.repository.TaskInputRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.request.TaskAnswerRequest;
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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TaskAnswerServiceImpl implements TaskAnswerService {
    private TaskRepository taskRepository;
    private TaskAnswerResultRepository taskAnswerResultRepository;
    private TaskAnswerResultTaskRepository taskAnswerResultTaskRepository;
    private TaskInputRepository taskInputRepository;
    private JdoodleService jdoodleService;
    private LanguageConverter languageConverter;

    @Autowired
    public TaskAnswerServiceImpl(
        TaskRepository taskRepository,
        TaskAnswerResultRepository taskAnswerResultRepository,
        TaskAnswerResultTaskRepository taskAnswerResultTaskRepository,
        JdoodleService jdoodleService,
        LanguageConverter languageConverter,
        TaskInputRepository taskInputRepository
    ) {
        this.taskRepository = taskRepository;
        this.taskAnswerResultRepository = taskAnswerResultRepository;
        this.taskAnswerResultTaskRepository = taskAnswerResultTaskRepository;
        this.jdoodleService = jdoodleService;
        this.languageConverter = languageConverter;
        this.taskInputRepository = taskInputRepository;
    }

    @Override
    public Long getScoreByResult(Long resultId) {
        return taskAnswerResultRepository.findById(resultId)
            .map(com.example.demo.entity.TaskAnswerResult::getScore)
            .orElseThrow();
    }

    public TaskAnswerResult processAnswers(List<TaskAnswer> answers, List<TaskCompilableAnswer> compilableAnswers) {
        var scoreSimpleTasks = processAnswers(answers);
        var scoreCompilableTasks = processCompilableAnswer(compilableAnswers);
        var result = taskAnswerResultRepository.save(com.example.demo.entity.TaskAnswerResult.builder()
            .score((long) scoreCompilableTasks.score + scoreSimpleTasks.score)
            .build());
        Stream.concat(scoreCompilableTasks.tasks.stream(), scoreSimpleTasks.tasks.stream())
                .map(task -> TaskAnswerResultTask.builder()
                        .id(TaskAnswerResultTaskKey.builder()
                                .taskAnswerResultId(result.getId())
                                .taskId(task.getId())
                                .build())
                        .taskAnswerResult(result)
                        .task(task)
                        .build()
                ).forEach(taskAnswerResultTaskRepository::save);
        return TaskAnswerResult.builder()
                .resultUrl("http://localhost:8080/answer/" + result.getId())
                .build();
    }

    private TaskAnswerScore processAnswers(List<TaskAnswer> answers) {
        Map<Long, Task> taskIdToTask = StreamSupport.stream(
            taskRepository.findAllById(
                answers.stream()
                    .map(TaskAnswer::getTaskId)
                    .collect(Collectors.toSet()
            )
        ).spliterator(), false)
        .collect(Collectors.toMap(Task::getId, Function.identity()));

        List<Task> correctAnsweredTasks = new ArrayList<>();

        answers.stream()
            .filter(taskAnswer -> {
                var task = taskIdToTask.get(taskAnswer.getTaskId());
                boolean correctAnswered = task.getAnswers().stream()
                    .map(com.example.demo.entity.TaskAnswer::getAnswer)
                    .collect(Collectors.toSet()).contains(taskAnswer.getAnswer());
                if (correctAnswered) {
                    correctAnsweredTasks.add(task);
                }
                return correctAnswered;
            })
            .collect(Collectors.toSet());

        return new TaskAnswerScore(
            correctAnsweredTasks.stream()
                .mapToInt(Task::getLevel)
                .sum(),
            taskIdToTask.values().stream().toList()
        );
    }

    private TaskAnswerScore processCompilableAnswer(List<TaskCompilableAnswer> answers) {
        Map<Long, Task> taskIdToTask = StreamSupport.stream(
                taskRepository.findAllById(
                    answers.stream()
                        .map(TaskCompilableAnswer::getTaskId)
                        .collect(Collectors.toSet()
                    )
                ).spliterator(), false)
            .collect(Collectors.toMap(Task::getId, Function.identity()));
        int score = 0;
        answers.stream()
                .forEach(answer -> {
                    var task = taskIdToTask.get(answer.getTaskId());
                    var result = executeCode(task, answer.getAnswer(), languageConverter.convert(answer.getLang()));
                });
        return new TaskAnswerScore(
                score,
                taskIdToTask.values().stream().toList()
        );
    }

    private String executeCode(Task task, String code, Language language) {
        var input = taskInputRepository.findTaskInputByTaskId(task.getId());
        if(input == null) {
            throw new IllegalStateException(String.format("No input for compilable task %s", task.getId()));
        }
        return jdoodleService.executeCode(code, input.getValue(), language);
    }
    private record TaskAnswerScore(int score, List<Task> tasks){}
}
