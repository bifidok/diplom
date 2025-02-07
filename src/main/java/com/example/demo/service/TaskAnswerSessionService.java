package com.example.demo.service;


import com.example.demo.entity.TaskAnswerSession;

import java.util.List;

public interface TaskAnswerSessionService {
    List<TaskAnswerSession> findSessionsByUser(Long userId);
}
