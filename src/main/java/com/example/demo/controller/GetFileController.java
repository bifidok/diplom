package com.example.demo.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("getfile")
public class GetFileController {
    private static final String DEFAULT_FILE_PATH = "C:\\Users\\striz\\IdeaProjects\\demo\\files\\";

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(
        @RequestParam String name,
        @RequestParam(value = "isDownload", required = false) boolean isDownload
    ) throws IOException {
        var path = Path.of(String.format(DEFAULT_FILE_PATH + "%s", name));
        var response = ResponseEntity.ok();
        if(isDownload) {
            response.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"");
        }
        return response
            .body(new ByteArrayResource(Files.readAllBytes(path)));
    }
}
