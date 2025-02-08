package com.example.demo.controller;

import com.example.demo.dto.LoginForm;
import com.example.demo.entity.User;
import com.example.demo.service.SessionService;
import com.example.demo.service.UserService;
import com.example.demo.utils.SessionUtils;
import com.example.demo.validation.UserValidation;
import com.example.demo.validation.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final SessionService sessionService;
    private final UserService userService;

    @Autowired
    public RegistrationController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<List<String>> register(
        @RequestBody LoginForm loginForm,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        var userOptional = userService.findByLogin(loginForm.login);
        if (userOptional.isPresent()) {
            return ResponseEntity.badRequest()
                .body(List.of(String.format("User already exist %s", loginForm.login)));
        }
        var errors = UserValidation.validate(loginForm.login, loginForm.password);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(
                    errors.stream()
                        .map(ValidationError::message)
                        .toList()
                );
        }
        User user = userService.save(loginForm.login, loginForm.password);
        var session = sessionService.save(user.getId());
        SessionUtils.clearCookies(request, response);
        SessionUtils.addCookie(session, response);
        return ResponseEntity.ok()
            .body(List.of("Registered"));
    }
}
