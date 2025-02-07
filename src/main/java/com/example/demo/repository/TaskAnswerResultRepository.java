package com.example.demo.repository;

import com.example.demo.entity.TaskAnswerResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskAnswerResultRepository extends CrudRepository<TaskAnswerResult, Long> {
    Optional<TaskAnswerResult> findTaskAnswerResultByTaskAnswerSessionId(Long sessionId);
}
