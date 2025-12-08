package com.eduquizz.backend.controllers;

import com.eduquizz.backend.entities.QuizAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eduquizz.backend.servicies.QuizAttemptService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/quizAttempt")
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
    @GetMapping("/student/classroom/{classroomId}")
    public ResponseEntity<?> getStudentAttemptsByClassroom(
            @PathVariable Long classroomId,
            @RequestParam Long studentId) {
        try {
            List<QuizAttempt> attempts = quizAttemptService.getStudentAttemptsByClassroom(studentId, classroomId);
            return ResponseEntity.ok(attempts);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
