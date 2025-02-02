package com.example.demo.repository;

import com.example.demo.entity.TaskProgram;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskProgramRepository extends CrudRepository<TaskProgram, Long> {
    TaskProgram findByTaskId(Long taskId);
}
