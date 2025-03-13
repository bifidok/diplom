package com.example.demo.service;

import com.example.demo.dto.CodeExecutionMessage;
import com.example.demo.enums.Language;

public interface JdoodleService {
    CodeExecutionMessage executeCode(String code, String input, Language lang);
}
