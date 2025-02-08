package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AccountDetails {
    private String login;
    private List<TaskAnswerSession> sessions;
}
