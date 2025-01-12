package com.example.demo.controller;

import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.request.TaskAnswerRequest;
import com.example.demo.service.TaskAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
public class TaskAnswerController {
    private TaskAnswerService taskAnswerService;
    @Autowired
    public TaskAnswerController(TaskAnswerService taskAnswerService) {
        this.taskAnswerService = taskAnswerService;
    }

    @GetMapping("/{id}")
    public Long getResult(@PathVariable Long id) {
        return taskAnswerService.getScoreByResult(id);
    }

    @PostMapping
    public TaskAnswerResult answer(@RequestBody TaskAnswerRequest answers) {
        return taskAnswerService.processAnswers(
            answers.getTaskToAnswer().entrySet().stream()
                .map(entry ->
                    TaskAnswer.builder()
                        .taskId(entry.getKey())
                        .answer(entry.getValue())
                        .build()
                )
                .toList(),
            answers.getTaskToCompilableAnswer().entrySet().stream()
                .map(entry ->
                    TaskCompilableAnswer.builder()
                        .taskId(entry.getKey())
                        .answer(entry.getValue().getAnswer())
                        .lang(entry.getValue().getLang())
                        .build()
                )
                .toList()
        );
    }
}
