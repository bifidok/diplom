package com.example.demo.service;

import com.example.demo.enums.Language;

public interface JdoodleService {
    String executeCode(String code, String input, Language lang);
}
