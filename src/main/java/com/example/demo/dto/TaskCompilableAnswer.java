package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskCompilableAnswer {
    private Long taskId;
    private String answer;
    private String lang;
}
