package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskInput {
    private String value;
    private boolean isNumeric;
}
