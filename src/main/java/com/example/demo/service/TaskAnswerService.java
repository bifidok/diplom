package com.example.demo.service;

import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;
import com.example.demo.dto.TaskAnswerSession;
import com.example.demo.dto.TaskCompilableAnswer;
import com.example.demo.entity.User;
import jakarta.annotation.Nullable;

import java.util.List;

public interface TaskAnswerService {
    TaskAnswerSession getScoreBySession(Long sessionId);
    TaskAnswerResult processAnswers(
        String tasksHashcode,
        List<TaskAnswer> answers,
        List<TaskCompilableAnswer> compilableAnswers,
        @Nullable User user
    );
}
