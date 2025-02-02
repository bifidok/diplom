package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "task_input")
@Getter
public class TaskInput {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @Column(name = "value")
    private String value;
}