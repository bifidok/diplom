package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskFile {
    private Long id;
    private String name;
}
