package com.example.demo.controller;

import com.example.demo.dto.AccountDetails;
import com.example.demo.dto.TaskAnswerSession;
import com.example.demo.service.SessionService;
import com.example.demo.service.TaskAnswerService;
import com.example.demo.service.TaskAnswerSessionService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    private TaskAnswerSessionService taskAnswerSessionService;
    private SessionService sessionService;
    private TaskAnswerService taskAnswerService;

    @Autowired
    public AccountController(
        TaskAnswerSessionService taskAnswerSessionService,
        SessionService sessionService,
        TaskAnswerService taskAnswerService
    ) {
        this.taskAnswerSessionService = taskAnswerSessionService;
        this.sessionService = sessionService;
        this.taskAnswerService = taskAnswerService;
    }

    @GetMapping
    public ResponseEntity<AccountDetails> get(HttpServletRequest request) {
        Cookie cookieSession = SessionUtils.findSessionCookie(request);
        if (cookieSession == null) {
            return ResponseEntity.noContent().build();
        }
        String sessionId = cookieSession.getValue();
        var sessionOptional = sessionService.findById(Long.valueOf(sessionId));
        if (sessionOptional.isEmpty()) {
            throw new IllegalStateException(String.format("Session %s not found", sessionId));
        }
        var taskAnswerSessions = taskAnswerSessionService.findSessionsByUser(sessionOptional.get().getUser().getId());
        return ResponseEntity.ok()
            .body(
                AccountDetails.builder()
                    .sessions(
                        taskAnswerSessions.stream()
                            .map(session -> taskAnswerService.getScoreBySession(session.getId()))
                            .toList()
                    ).login(sessionOptional.get().getUser().getLogin())
                    .build()
            );
    }
}
