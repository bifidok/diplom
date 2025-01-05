package com.example.demo.repository;

import com.example.demo.entity.Version;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionRepository extends CrudRepository<Version, Long> {
    Optional<Version> findVersionByHashcode(String hashcode);
}
