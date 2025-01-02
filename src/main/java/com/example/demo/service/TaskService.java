package com.example.demo.service;

import com.example.demo.dto.Task;

import java.util.List;

public interface TaskService {
    List<Task> getTasks();

    Task getTask(Long id);
}
