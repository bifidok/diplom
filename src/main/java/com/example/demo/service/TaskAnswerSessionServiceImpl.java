package com.example.demo.service;

import com.example.demo.entity.TaskAnswerSession;
import com.example.demo.repository.TaskAnswerSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskAnswerSessionServiceImpl implements TaskAnswerSessionService{
    private TaskAnswerSessionRepository taskAnswerSessionRepository;

    public TaskAnswerSessionServiceImpl(TaskAnswerSessionRepository taskAnswerSessionRepository) {
        this.taskAnswerSessionRepository = taskAnswerSessionRepository;
    }

    @Override
    public List<TaskAnswerSession> findSessionsByUser(Long userId) {
        return taskAnswerSessionRepository.findByUserId(userId);
    }
}
