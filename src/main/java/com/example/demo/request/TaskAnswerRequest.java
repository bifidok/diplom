package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskAnswerRequest {
    private Map<Long, String> taskToAnswer;
}
