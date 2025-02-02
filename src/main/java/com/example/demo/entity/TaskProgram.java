package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "task_program")
@Getter
public class TaskProgram {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @Column
    private String programName;
}
