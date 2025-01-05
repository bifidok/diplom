package com.example.demo.repository;

import com.example.demo.entity.TaskVersion;
import com.example.demo.entity.TaskVersionKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskVersionRepository extends CrudRepository<TaskVersion, TaskVersionKey> {
    List<TaskVersion> findTaskVersionByVersionHashcodeContaining(String hashcode);
}
