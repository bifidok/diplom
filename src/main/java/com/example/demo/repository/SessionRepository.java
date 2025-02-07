package com.example.demo.repository;

import com.example.demo.entity.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
    @Query(
        value = "select * from session where user_id = :userId and expires_at = (select max(expires_at) from session where user_id = :userId)",
        nativeQuery = true
    )
    Optional<Session> findSessionByUserId(Long userId);
}
