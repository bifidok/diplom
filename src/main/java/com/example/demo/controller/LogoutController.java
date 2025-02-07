package com.example.demo.controller;

import com.example.demo.service.SessionService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutController {
    private final SessionService sessionService;

    @Autowired
    public LogoutController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SessionUtils.clearCookies(request, response);
        return ResponseEntity.ok()
            .body("Logged out");
    }
}
