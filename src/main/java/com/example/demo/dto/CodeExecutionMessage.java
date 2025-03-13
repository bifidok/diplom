package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CodeExecutionMessage {
    private String output;
    private String error;
}
