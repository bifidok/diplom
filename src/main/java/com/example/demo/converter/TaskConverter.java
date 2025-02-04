package com.example.demo.converter;

import com.example.demo.dto.Task;
import com.example.demo.dto.TaskDescription;
import com.example.demo.dto.TaskFile;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TaskConverter {
    private final static String IMAGE_REGEX = ".*\\.(jpg|jpeg|png|gif|bmp|tiff|webp|svg)$";

    @Nonnull
    public Task convert(com.example.demo.entity.Task task){
        return Task.builder()
            .id(task.getId())
            .description(convertDescription(task.getDescription()))
            .level(task.getLevel())
            .images(
                task.getFiles().stream()
                    .filter(this::isImage)
                    .map(
                        image -> TaskFile.builder()
                            .id(image.getId())
                            .name(image.getName())
                            .build()
                    ).collect(Collectors.toSet())
            )
            .files(
                task.getFiles().stream()
                    .filter(file -> !isImage(file))
                    .map(
                        file -> TaskFile.builder()
                            .id(file.getId())
                            .name(file.getName())
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

    private boolean isImage(com.example.demo.entity.TaskFile file) {
        Pattern pattern = Pattern.compile(IMAGE_REGEX, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(file.getName()).matches();
    }
}
