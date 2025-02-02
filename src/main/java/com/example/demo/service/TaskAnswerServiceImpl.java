package com.example.demo.service;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.entity.*;
import com.example.demo.enums.Language;
import com.example.demo.repository.*;
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
    private TaskProgramRepository taskProgramRepository;
    private TaskAnswerResultRepository taskAnswerResultRepository;
    private TaskAnswerResultTaskRepository taskAnswerResultTaskRepository;
    private TaskInputRepository taskInputRepository;
    private JdoodleService jdoodleService;
    private LanguageConverter languageConverter;
    private CodeExecutorService codeExecutorService;

    @Autowired
    public TaskAnswerServiceImpl(
        TaskRepository taskRepository,
        TaskAnswerResultRepository taskAnswerResultRepository,
        TaskAnswerResultTaskRepository taskAnswerResultTaskRepository,
        JdoodleService jdoodleService,
        LanguageConverter languageConverter,
        TaskInputRepository taskInputRepository,
        CodeExecutorService codeExecutorService,
        TaskProgramRepository taskProgramRepository
    ) {
        this.taskRepository = taskRepository;
        this.taskAnswerResultRepository = taskAnswerResultRepository;
        this.taskAnswerResultTaskRepository = taskAnswerResultTaskRepository;
        this.jdoodleService = jdoodleService;
        this.languageConverter = languageConverter;
        this.taskInputRepository = taskInputRepository;
        this.codeExecutorService = codeExecutorService;
        this.taskProgramRepository = taskProgramRepository;
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
        for(var answer : answers) {
            var task = taskIdToTask.get(answer.getTaskId());
            var isCorrect = executeCode(task, answer.getAnswer(), languageConverter.convert(answer.getLang()));
            if (isCorrect) {
                score += task.getLevel();
            }
        }
        return new TaskAnswerScore(
            score,
            taskIdToTask.values().stream().toList()
        );
    }

    private boolean executeCode(Task task, String code, Language language) {
        List<TaskInput> inputs = taskInputRepository.findTaskInputsByTaskId(task.getId());
        if (inputs.isEmpty()) {
            throw new IllegalStateException(String.format("No input for compilable task %s", task.getId()));
        }
        TaskProgram program = taskProgramRepository.findByTaskId(task.getId());
        if (program == null) {
            throw new IllegalStateException(String.format("No program for compilable task %s", task.getId()));
        }
        boolean isCorrect = true;
        for (var input : inputs) {
            var expected = codeExecutorService.executeProgram(input.getValue(), program.getProgramName());
            var actual = jdoodleService.executeCode(code, input.getValue(), language);
            if (!expected.equals(actual)) {
                isCorrect = false;
            }
        }
        return isCorrect;
    }
    private record TaskAnswerScore(int score, List<Task> tasks){}
}
