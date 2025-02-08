package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_answer_result")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAnswerResult {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long score;

    @OneToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private TaskAnswerSession taskAnswerSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", referencedColumnName = "id")
    private Version version;
}
