package com.eduquizz.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.servicies.QuizAttemptService;

@CrossOrigin("")
@RestController
@RequestMapping("/api/quizAttempt/")
public class QuizAttemptController {
    @Autowired
    private QuizAttemptService quizAttemptService;

    @GetMapping("")
    public ResponseEntity<?> getQuizAttemptByStudentIdAndQuizId(@RequestParam Long studentId, @RequestParam Long quizId)
    {
        try
        {
            return ResponseEntity.ok(quizAttemptService.getQuizAttemptByStudentIdAndQuizId(studentId, quizId));
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
