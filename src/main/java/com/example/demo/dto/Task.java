package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Task {

    private TaskDescription description;

    private Set<TaskAnswer> answers;

    private int level;
}
