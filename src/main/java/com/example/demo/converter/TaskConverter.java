package com.example.demo.converter;

import com.example.demo.dto.Task;
import com.example.demo.dto.TaskAnswer;
import com.example.demo.dto.TaskDescription;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskConverter {
    @Nonnull
    public Task convert(com.example.demo.entity.Task task){
        return Task.builder()
            .description(convertDescription(task.getDescription()))
            .answers(
                task.getAnswers().stream()
                    .map(this::convertAnswer)
                    .collect(Collectors.toSet())
            )
            .level(task.getLevel())
            .build();
    }

    public TaskDescription convertDescription(com.example.demo.entity.TaskDescription description){
        return TaskDescription.builder()
            .text(description.getText())
            .build();
    }

    public TaskAnswer convertAnswer(com.example.demo.entity.TaskAnswer answer){
        return TaskAnswer.builder()
                .answer(answer.getAnswer())
                .build();
    }
}
