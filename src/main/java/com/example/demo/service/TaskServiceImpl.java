package com.example.demo.service;

import com.example.demo.converter.TaskConverter;
import com.example.demo.dto.Task;
import com.example.demo.repository.TaskRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class TaskServiceImpl implements TaskService{
    private TaskRepository taskRepository;
    private TaskConverter taskConverter;

    @Autowired
    public TaskServiceImpl(
        TaskRepository repository,
        TaskConverter converter
    ) {
        this.taskConverter = converter;
        this.taskRepository = repository;
    }

    public List<Task> getTasks(){
        List<Task> result = new ArrayList<>();
        taskRepository.findAll()
            .forEach(entity -> {
                result.add(taskConverter.convert(entity));
            });
        return result;
    }

    public Task getTask(Long id){
        return taskRepository.findById(id)
            .map(taskConverter::convert)
            .orElse(null);
    }
}
