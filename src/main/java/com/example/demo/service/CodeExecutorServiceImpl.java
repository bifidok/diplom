package com.example.demo.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CodeExecutorServiceImpl implements CodeExecutorService{
    private final static String PROGRAMS_DIRECTORY = "programs";

    @Override
    public String executeProgram(String input, String programFile) {
        String result = null;
        try {
            input = input + " ";
            ProcessBuilder runProcess = getProcessBuilder(programFile);
            Process run = runProcess.start();
            run.getOutputStream().write(input.getBytes());
            run.getOutputStream().flush();
            if (run.waitFor(5, TimeUnit.SECONDS)) {
                var output = run.getInputStream();
                result = new String(output.readAllBytes(), StandardCharsets.UTF_8);
                result = result.substring(0, result.length() - 2);
            } else {
                run.destroy();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static ProcessBuilder getProcessBuilder(String programFile) throws IOException, InterruptedException {
        ProcessBuilder compileProcess = new ProcessBuilder("javac", programFile + ".java");
        compileProcess.directory(new File(PROGRAMS_DIRECTORY));
        Process compile = compileProcess.start();
        compile.waitFor();

        List<String> command = Arrays.asList("java", programFile);
        ProcessBuilder runProcess = new ProcessBuilder(command);
        runProcess.directory(new File(PROGRAMS_DIRECTORY));
        return runProcess;
    }
}
