package com.example.demo.service;

import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskAnswerResult;

import java.util.List;

public interface TaskAnswerService {
    Long getScoreByResult(Long resultId);
    TaskAnswerResult processAnswers(List<TaskAnswer> answers);
}
