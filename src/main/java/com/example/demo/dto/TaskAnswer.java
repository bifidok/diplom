package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskAnswer {
    private Long taskId;
    private String answer;
}
