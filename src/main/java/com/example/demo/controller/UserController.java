package com.example.demo.controller;

import com.example.demo.dto.User;
import com.example.demo.entity.Session;
import com.example.demo.service.SessionService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final SessionService sessionService;

    public UserController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<User> get(HttpServletRequest request) {
        Cookie cookieSession = SessionUtils.findSessionCookie(request);
        if (cookieSession == null) {
            return ResponseEntity.noContent().build();
        }
        String sessionId = cookieSession.getValue();
        Optional<Session> sessionOptional = sessionService.findById(Long.valueOf(sessionId));
        if (sessionOptional.isEmpty() || sessionOptional.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.noContent().build();
        }
        var user = sessionOptional.get().getUser();
        return ResponseEntity.ok()
            .body(User.builder()
                .login(user.getLogin())
                .build());
    }
}
