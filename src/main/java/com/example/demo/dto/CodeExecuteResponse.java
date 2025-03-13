package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeExecuteResponse {
    private String output;
    private String error;
}
