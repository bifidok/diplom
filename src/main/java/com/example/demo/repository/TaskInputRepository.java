package com.example.demo.repository;

import com.example.demo.entity.TaskInput;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskInputRepository extends CrudRepository<TaskInput, Long> {
    List<TaskInput> findTaskInputsByTaskId(Long taskId);
}
