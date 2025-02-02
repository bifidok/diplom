package com.example.demo.repository;

import com.example.demo.entity.TaskInput;
import org.springframework.data.repository.CrudRepository;

public interface TaskInputRepository extends CrudRepository<TaskInput, Long> {
    TaskInput findTaskInputByTaskId(Long taskId);
}
