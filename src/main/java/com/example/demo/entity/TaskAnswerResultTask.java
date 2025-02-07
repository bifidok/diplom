package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_answer_result_task")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskAnswerResultTask {
    @EmbeddedId
    private TaskAnswerResultTaskKey id;
    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @MapsId("taskAnswerResultId")
    @JoinColumn(name = "task_answer_result_id")
    private TaskAnswerResult taskAnswerResult;

    @Column
    private Long score;
}
