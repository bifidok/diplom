package com.example.demo.controller;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.dto.CodeExecuteRequest;
import com.example.demo.dto.CodeExecuteResponse;
import com.example.demo.service.JdoodleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/execute")
public class CodeExecuteController {
    private JdoodleService jdoodleService;
    private LanguageConverter languageConverter;

    public CodeExecuteController(JdoodleService jdoodleService, LanguageConverter languageConverter) {
        this.jdoodleService = jdoodleService;
        this.languageConverter = languageConverter;
    }

    @PostMapping
    public ResponseEntity<CodeExecuteResponse> execute(@RequestBody CodeExecuteRequest codeExecuteRequest) {
        var message = jdoodleService.executeCode(
            codeExecuteRequest.getCode(),
            "",
            languageConverter.convert(codeExecuteRequest.getLang())
        );
        return ResponseEntity.ok(CodeExecuteResponse.builder()
            .output(message.getOutput())
            .error(message.getError())
            .build());
    }
}
