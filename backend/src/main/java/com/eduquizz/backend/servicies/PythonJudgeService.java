package com.eduquizz.backend.servicies;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Service
public class PythonJudgeService 
{
    public record TestResult(boolean success, String message) {
    }

    public TestResult executePythonCode(String code, String input, String expectedOutput) {
        Path tempDir = null;
        try
        {
            tempDir = Files.createTempDirectory("python_judge_");

            File sourceFile = tempDir.resolve("script.py").toFile();
            try (FileWriter writer = new FileWriter(sourceFile))
            {
                writer.write(code);
            }

            ProcessBuilder runPb = new ProcessBuilder("python", "-u", sourceFile.getAbsolutePath());
            runPb.directory(tempDir.toFile());

            Process process = runPb.start();

            if(input != null && !input.isEmpty())
            {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream())))
                {
                    writer.write(input);
                    writer.flush();
                }
            }

            if(!process.waitFor(3, TimeUnit.SECONDS))
            {
                process.destroyForcibly();
                return new TestResult(false, "Time limit exceeded");
            }

            String actualOutput = readStream(process.getInputStream()).trim();
            String errorOutput = readStream(process.getErrorStream()).trim();
            
            if(process.exitValue() != 0)
            {
                return new TestResult(false, "Runtime Error: " + errorOutput);
            }

            String expectedTrimmed = expectedOutput != null ? expectedOutput.trim() : "";
            if(actualOutput.equals(expectedTrimmed))
            {
                return new TestResult(true, "Success");
            }
            else
            {
                return new TestResult(false, "Wrong Answer. Expected: '" + expectedTrimmed + "', but got: '" + actualOutput + "'");
            }
        } catch (Exception e)
        {
            return new TestResult(false, "Error during execution: " + e.getMessage());
        } finally {
            if (tempDir != null) {
                deleteDirectoryRecursively(tempDir.toFile());
            }
        }

    }

    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void deleteDirectoryRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteDirectoryRecursively(child);
            }
        }
        file.delete();
    }
}
