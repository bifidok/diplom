package com.example.demo.controller;

import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskAnswerSession;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.entity.Session;
import com.example.demo.entity.User;
import com.example.demo.request.TaskAnswerRequest;
import com.example.demo.service.SessionService;
import com.example.demo.service.TaskAnswerService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/answer")
public class TaskAnswerController {
    private TaskAnswerService taskAnswerService;
    private SessionService sessionService;

    @Autowired
    public TaskAnswerController(TaskAnswerService taskAnswerService, SessionService sessionService) {
        this.taskAnswerService = taskAnswerService;
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}")
    public TaskAnswerSession getResult(@PathVariable Long id) {
        return taskAnswerService.getScoreBySession(id);
    }

    @PostMapping
    public TaskAnswerResult answer(@RequestBody TaskAnswerRequest answers, HttpServletRequest request) {
        Cookie cookieSession = SessionUtils.findSessionCookie(request);
        User user = null;
        if (cookieSession != null) {
            String sessionId = cookieSession.getValue();
            Optional<Session> sessionOptional = sessionService.findById(Long.valueOf(sessionId));
            if(sessionOptional.isPresent()) {
                user = sessionOptional.get().getUser();
            }
        }
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
                .toList(),
                user
        );
    }
}
