package com.example.demo.validation;

import java.util.ArrayList;
import java.util.List;

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
        return errors;
    }
}
