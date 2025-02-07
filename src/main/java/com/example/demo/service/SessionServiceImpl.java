package com.example.demo.service;

import com.example.demo.entity.Session;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService {
    private final static Long HOURS_SESSION_AVAILABLE = 3L;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    public Optional<Session> findByUserId(Long userId) {
        return sessionRepository.findSessionByUserId(userId);
    }

    @Override
    public Session save(Long userId) {
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException(String.format("User %s not found while session registration", userId));
        }
        return sessionRepository.save(
            Session.builder()
                .user(userOptional.get())
                .expiresAt(LocalDateTime.now().plusHours(HOURS_SESSION_AVAILABLE))
                .build()
        );
    }

    @Override
    public Session updateSession(Session session) {
        session.setExpiresAt(LocalDateTime.now().plusHours(HOURS_SESSION_AVAILABLE));
        return sessionRepository.save(session);
    }
}
