package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Task {
    private Long id;
    private TaskDescription description;
    private Set<TaskImage> images;
    private int level;
}
