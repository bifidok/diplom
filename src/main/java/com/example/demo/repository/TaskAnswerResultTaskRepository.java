package com.example.demo.repository;

import com.example.demo.entity.TaskAnswerResultTask;
import com.example.demo.entity.TaskAnswerResultTaskKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAnswerResultTaskRepository extends CrudRepository<TaskAnswerResultTask, TaskAnswerResultTaskKey> {
}
