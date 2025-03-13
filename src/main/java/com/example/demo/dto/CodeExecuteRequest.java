package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeExecuteRequest {
    private String code;
    private String lang;
}
