package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_version")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskVersion {
    @EmbeddedId
    private TaskVersionKey id;
    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    Task task;

    @ManyToOne
    @MapsId("versionId")
    @JoinColumn(name = "version_id")
    Version version;
}
