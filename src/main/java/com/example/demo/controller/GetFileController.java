package com.example.demo.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("getfile")
public class GetFileController {
    private static final String DEFAULT_FILE_PATH = "C:\\Users\\striz\\IdeaProjects\\demo\\images\\";
    @GetMapping
    public ByteArrayResource getFile(@RequestParam String name) throws IOException {
        var path = Path.of(String.format(DEFAULT_FILE_PATH + "%s.png", name));
        return new ByteArrayResource(Files.readAllBytes(path));
    }
}
