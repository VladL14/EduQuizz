package com.eduquizz.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;

import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.entities.QuizAttempt;
import com.eduquizz.backend.dtos.QuestionRequest;
import com.eduquizz.backend.dtos.QuizRequest;
import com.eduquizz.backend.dtos.SubmitQuizRequest;
import com.eduquizz.backend.servicies.QuizAttemptService;
import com.eduquizz.backend.servicies.QuizService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizAttemptService quizAttemptService;

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest Request) {
        try {
            Quiz quiz = quizService.createQuiz(Request);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<?> getQuizzesByClassroom(@PathVariable Long classroomId) {
        try{
            List<Quiz> quizzes = quizService.getQuizzesByClassroom(classroomId);
            return ResponseEntity.ok(quizzes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody QuizRequest request) {
        try{
            Quiz updateQuiz = quizService.updateQuiz(id, request);
            return ResponseEntity.ok(updateQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok("Quiz deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{quizId}/start")
    public ResponseEntity<?> startQuiz(@PathVariable Long quizId, @RequestParam Long studentId)
    {
        try {
            return ResponseEntity.ok(quizAttemptService.startQuiz(studentId, quizId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId, @RequestParam Long studentId, @RequestBody List<SubmitQuizRequest> answers)
    {
        try{
            QuizAttempt attempt = quizAttemptService.submitQuiz(studentId, quizId, answers);
            return ResponseEntity.ok(attempt);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
