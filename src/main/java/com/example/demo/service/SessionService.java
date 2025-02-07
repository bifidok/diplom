package com.example.demo.service;

import com.example.demo.entity.Session;

import java.util.Optional;

public interface SessionService {
    Optional<Session> findById(Long id);
    Optional<Session> findByUserId(Long userId);
    Session save(Long userId);
    Session updateSession(Session session);
}
