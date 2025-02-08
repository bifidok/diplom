package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class TaskAnswerSession {
    private Long commonScore;
    private String hashcode;
    private Map<Long,TaskDetail> taskIdToDetail;
}
