package com.example.demo.repository;

import com.example.demo.entity.TaskAnswerSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAnswerSessionRepository extends CrudRepository<TaskAnswerSession, Long> {
}
