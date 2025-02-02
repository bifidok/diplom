package com.example.demo.converter;

import com.example.demo.enums.Language;
import com.example.demo.properties.jdoodle.LanguageVersions;
import org.springframework.stereotype.Component;

@Component
public class LanguageConverter {
    public Language convert(String language){
        return switch (language) {
            case "JAVA" -> Language.JAVA;
            case "C_SHARP" -> Language.C_SHARP;
            case "C" -> Language.C;
            case "C_PLUS" -> Language.C_PLUS;
            case "PASCAL" -> Language.PASCAL;
            case "PYTHON" -> Language.PYTHON;
            default -> throw new IllegalArgumentException("Unknown language");
        };
    }

    public com.example.demo.properties.jdoodle.Language toPropertiesLanguage(LanguageVersions languageVersions, Language language){
        return switch (language) {
            case C -> languageVersions.getC();
            case C_PLUS -> languageVersions.getCPlus();
            case C_SHARP -> languageVersions.getCSharp();
            case PASCAL -> languageVersions.getPascal();
            case PYTHON -> languageVersions.getPython();
            case JAVA -> languageVersions.getJava();
            default -> throw new IllegalArgumentException("Unknown language");
        };
    }
}
