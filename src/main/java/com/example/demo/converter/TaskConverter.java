package com.example.demo.converter;

import com.example.demo.dto.Task;
import com.example.demo.dto.TaskDescription;
import com.example.demo.dto.TaskImage;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskConverter {

    @Nonnull
    public Task convert(com.example.demo.entity.Task task){
        return Task.builder()
            .id(task.getId())
            .description(convertDescription(task.getDescription()))
            .level(task.getLevel())
            .images(
                task.getImages().stream()
                    .map(
                        image -> TaskImage.builder()
                            .id(image.getId())
                            .name(image.getName())
                            .build()
                    ).collect(Collectors.toSet())
            )
            .build();
    }

    @Nonnull
    public TaskDescription convertDescription(com.example.demo.entity.TaskDescription description){
        return TaskDescription.builder()
            .text(description.getText())
            .build();
    }
}
