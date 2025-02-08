package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaskDetail {
    private Long index;
    private Long score;
}
