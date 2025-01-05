package com.example.demo.controller;

import com.example.demo.dto.Task;
import com.example.demo.service.TaskService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/task")
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{hashcode}")
    public List<Task> getTasks(@PathVariable String hashcode){
        return taskService.getTasks(hashcode);
    }

    @GetMapping("/generator")
    public String generateRandomVersion(){
        return taskService.generateRandomVersion();
    }
}
