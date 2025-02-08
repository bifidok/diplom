package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TaskVersionKey implements Serializable {
    @Column(name = "task_id")
    Long taskId;

    @Column(name = "version_id")
    Long versionId;
}
