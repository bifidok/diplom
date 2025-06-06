package com.example.demo.service;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.dto.TaskDetail;
import com.example.demo.entity.*;
import com.example.demo.enums.Language;
import com.example.demo.properties.UrlProperties;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TaskAnswerServiceImpl implements TaskAnswerService {
    private TaskRepository taskRepository;
    private TaskVersionRepository taskVersionRepository;
    private TaskProgramRepository taskProgramRepository;
    private TaskAnswerResultRepository taskAnswerResultRepository;
    private TaskAnswerResultTaskRepository taskAnswerResultTaskRepository;
    private TaskAnswerSessionRepository taskAnswerSessionRepository;
    private TaskInputRepository taskInputRepository;
    private JdoodleService jdoodleService;
    private LanguageConverter languageConverter;
    private CodeExecutorService codeExecutorService;
    private VersionRepository versionRepository;
    private UrlProperties urlProperties;

    @Autowired
    public TaskAnswerServiceImpl(
        TaskRepository taskRepository,
        TaskAnswerResultRepository taskAnswerResultRepository,
        TaskAnswerResultTaskRepository taskAnswerResultTaskRepository,
        JdoodleService jdoodleService,
        LanguageConverter languageConverter,
        TaskInputRepository taskInputRepository,
        CodeExecutorService codeExecutorService,
        TaskProgramRepository taskProgramRepository,
        TaskAnswerSessionRepository taskAnswerSessionRepository,
        VersionRepository versionRepository,
        TaskVersionRepository taskVersionRepository,
        UrlProperties urlProperties) {
        this.taskRepository = taskRepository;
        this.taskAnswerResultRepository = taskAnswerResultRepository;
        this.taskAnswerResultTaskRepository = taskAnswerResultTaskRepository;
        this.jdoodleService = jdoodleService;
        this.languageConverter = languageConverter;
        this.taskInputRepository = taskInputRepository;
        this.codeExecutorService = codeExecutorService;
        this.taskProgramRepository = taskProgramRepository;
        this.taskAnswerSessionRepository = taskAnswerSessionRepository;
        this.versionRepository = versionRepository;
        this.taskVersionRepository = taskVersionRepository;
        this.urlProperties = urlProperties;
    }

    @Override
    public com.example.demo.dto.TaskAnswerSession getScoreBySession(Long sessionId) {
        var taskAnswerResultOptional = taskAnswerResultRepository.findTaskAnswerResultByTaskAnswerSessionId(sessionId);
        if (taskAnswerResultOptional.isEmpty()) {
            throw new IllegalStateException(String.format("Session not exist %s", sessionId));
        }
        var taskAnswerResultTask = taskAnswerResultTaskRepository.findTaskAnswerResultTaskByTaskAnswerResultId(
            taskAnswerResultOptional.get().getId()
        );

        var taskIdToIndex = taskVersionRepository.findTaskVersionByVersionHashcode(
            taskAnswerResultOptional.get().getVersion().getHashcode()
        ).stream()
            .collect(Collectors.toMap(
                taskVersion -> taskVersion.getId().getTaskId(),
                taskVersion -> taskVersion.getIndex()
            ));
        return com.example.demo.dto.TaskAnswerSession.builder()
            .hashcode(taskAnswerResultOptional.get().getVersion().getHashcode())
            .commonScore(taskAnswerResultOptional.get().getScore())
            .taskIdToDetail(
                taskAnswerResultTask.stream()
                    .collect(Collectors.toMap(
                        resultTask -> resultTask.getTask().getId(),
                        resultTask -> TaskDetail.builder()
                            .score(resultTask.getScore())
                            .index(taskIdToIndex.get(resultTask.getTask().getId()))
                            .build()
                    ))
            )
            .build();
    }

    public TaskAnswerResult processAnswers(
        String tasksHashcode,
        List<TaskAnswer> answers,
        List<TaskCompilableAnswer> compilableAnswers,
        User user
    ) {
        var scoreSimpleTasks = processAnswers(answers);
        var scoreCompilableTasks = processCompilableAnswer(compilableAnswers);
        var taskAnswerSession = taskAnswerSessionRepository.save(TaskAnswerSession.builder()
            .user(user)
            .build());
        var taskAnswerResult = taskAnswerResultRepository.save(com.example.demo.entity.TaskAnswerResult.builder()
            .score((long) scoreCompilableTasks.score + scoreSimpleTasks.score)
            .taskAnswerSession(taskAnswerSession)
            .version(versionRepository.findVersionByHashcode(tasksHashcode).get())
            .build());
        Stream.concat(scoreCompilableTasks.taskToScore.entrySet().stream(), scoreSimpleTasks.taskToScore.entrySet().stream())
            .map(taskToScore -> TaskAnswerResultTask.builder()
                .id(TaskAnswerResultTaskKey.builder()
                    .taskAnswerResultId(taskAnswerResult.getId())
                    .taskId(taskToScore.getKey().getId())
                    .build())
                .taskAnswerResult(taskAnswerResult)
                .task(taskToScore.getKey())
                .score(taskToScore.getValue())
                .build()
            ).forEach(taskAnswerResultTaskRepository::save);
        return TaskAnswerResult.builder()
            .resultUrl(urlProperties.getUrl() + ":8080/answer/" + taskAnswerSession.getId())
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

        Set<Task> correctAnsweredTasks = new HashSet<>();

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
            taskIdToTask.values().stream()
                .collect(Collectors.toMap(
                    task -> task,
                    task -> (long) (correctAnsweredTasks.contains(task) ? task.getLevel() : 0))
                )
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
        Set<Task> correctAnsweredTasks = new HashSet<>();
        for(var answer : answers) {
            var task = taskIdToTask.get(answer.getTaskId());
            var isCorrect = executeCode(task, answer.getAnswer(), languageConverter.convert(answer.getLang()));
            if (isCorrect) {
                score += task.getLevel();
                correctAnsweredTasks.add(task);
            }
        }
        return new TaskAnswerScore(
            score,
            taskIdToTask.values().stream()
                .collect(Collectors.toMap(
                    task -> task,
                    task -> (long) (correctAnsweredTasks.contains(task) ? task.getLevel() : 0))
                )
        );
    }

    private boolean executeCode(Task task, String code, Language language) {
        if(code.isEmpty()) return false;
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
            var actual = jdoodleService.executeCode(code, input.getValue(), language).getOutput();
            if (!expected.equals(actual)) {
                isCorrect = false;
            }
        }
        return isCorrect;
    }
    private record TaskAnswerScore(int score, Map<Task, Long> taskToScore){}
}
