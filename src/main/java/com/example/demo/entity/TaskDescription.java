package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "task_description")
@Getter
public class TaskDescription {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @Column(name = "text")
    private String text;
}
