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

    @GetMapping("/{id}")
    public ResponseEntity<?> getAttemptById(@PathVariable Long id) {
        try {
            QuizAttempt attempt = quizAttemptService.getAttemptById(id);
            return ResponseEntity.ok(attempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{attemptId}/grade/{questionId}")
    public ResponseEntity<?> gradeQuestion(
            @PathVariable Long attemptId,
            @PathVariable Long questionId,
            @RequestParam Integer points) {
        try {
            QuizAttempt updatedAttempt = quizAttemptService.gradeQuizAttempt(attemptId, questionId, points);
            return ResponseEntity.ok(updatedAttempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}/quiz/{quizId}")
    public ResponseEntity<?> getAttemptByStudentAndQuiz(@PathVariable Long studentId, @PathVariable Long quizId) {
        try {
            QuizAttempt attempt = quizAttemptService.getAttemptByStudentAndQuiz(studentId, quizId);
            return ResponseEntity.ok(attempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}