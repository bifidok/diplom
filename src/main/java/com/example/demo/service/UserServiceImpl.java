package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.validation.UserValidation;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Override
    public User save(String login, String password) {
        var userOptional = userRepository.findUserByLogin(login);
        if (userOptional.isPresent()) {
            throw new IllegalStateException(String.format("Trying to save existing user %s", login));
        }
        return userRepository.save(
            User.builder()
                .login(login)
                .password(password)
                .build()
        );
    }
}
