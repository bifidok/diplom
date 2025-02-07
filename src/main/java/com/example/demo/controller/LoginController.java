package com.example.demo.controller;

import com.example.demo.dto.LoginForm;
import com.example.demo.entity.Session;
import com.example.demo.entity.User;
import com.example.demo.service.SessionService;
import com.example.demo.service.UserService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final SessionService sessionService;
    private final UserService userService;

    @Autowired
    public LoginController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> login(
        @RequestBody LoginForm loginForm,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        var userOptional = userService.findByLogin(loginForm.login);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                .body("User not exist");
        }
        if (!userOptional.get().getPassword().equals(loginForm.password)) {
            return ResponseEntity.badRequest()
                .body("Not correct password");
        }
        var userId = userOptional.get().getId();
        var lastSession = sessionService.findByUserId(userId);
        Session session;
        if (lastSession.isPresent() && !lastSession.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            session = sessionService.updateSession(lastSession.get());
        } else {
            session = sessionService.save(userId);
        }
        SessionUtils.clearCookies(request, response);
        SessionUtils.addCookie(session, response);
        return ResponseEntity.ok()
            .body("Authenticated");
    }
}
