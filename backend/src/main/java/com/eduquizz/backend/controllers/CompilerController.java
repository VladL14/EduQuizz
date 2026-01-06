package com.eduquizz.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.servicies.PythonJudgeService;
import com.eduquizz.backend.servicies.PythonJudgeService.TestResult;


@RestController
@RequestMapping("/api/compiler")
@CrossOrigin(origins = "http://localhost:4200")
public class CompilerController {
    
    @Autowired
    private PythonJudgeService pythonJudgeService;

    public static class SubmissionRequest {
        public String code;
        public String input;
        public String expectedOutput;
    }

    @PostMapping("/run")
    public ResponseEntity<TestResult> runCode(@RequestBody SubmissionRequest request) {
        TestResult result = pythonJudgeService.executePythonCode(request.code, request.input, request.expectedOutput);
        return ResponseEntity.ok(result);
    }
}
