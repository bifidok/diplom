package com.example.demo.validation;

import com.example.demo.dto.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserValidation {
    private static final Integer LOGIN_MIN_LENGTH = 4;
    private static final Integer PASSWORD_MIN_LENGTH = 4;

    public static List<ValidationError> validate(String login, String password){
        List<ValidationError> errors = new ArrayList<>();
        if (login.length() < LOGIN_MIN_LENGTH) {
            errors.add(new ValidationError(String.format("Логин должен быть больше %s символов", LOGIN_MIN_LENGTH)));
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            errors.add(new ValidationError(String.format("Пароль должен быть больше %s символов", PASSWORD_MIN_LENGTH)));
        }
        // Проверка на отсутствие пробелов в пароле
        if (password.contains(" ")) {
            errors.add(new ValidationError("Пароль не должен содержать пробелы"));
        }

        // Проверка на наличие хотя бы одной цифры
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            errors.add(new ValidationError("Пароль должен содержать хотя бы одну цифру"));
        }

        // Проверка на наличие хотя бы одной заглавной буквы
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add(new ValidationError("Пароль должен содержать хотя бы одну заглавную букву"));
        }

        // Проверка на наличие хотя бы одной строчной буквы
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            errors.add(new ValidationError("Пароль должен содержать хотя бы одну строчную букву"));
        }

        // Проверка на наличие хотя бы одного специального символа
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            errors.add(new ValidationError("Пароль должен содержать хотя бы один специальный символ (!@#$%^&*(),.?\":{}|<>)"));
        }
        return errors;
    }
}
