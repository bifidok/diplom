package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "task_input")
@Data
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
    @Column(name = "is_numeric")
    private boolean isNumeric;
}