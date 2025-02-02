package com.example.demo.enums;

public enum Language {
    JAVA("java"),
    C_SHARP("csharp"),
    C("c"),
    C_PLUS("cpp"),
    PASCAL("pascal"),
    PYTHON("python3"),
    ;
    private final String name;

    Language(String name) {
        this.name = name;
    }
}
