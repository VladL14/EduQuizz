package com.eduquizz.backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eduquizz.backend.utils.AttemptStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*; 

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    private Double grade;
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentResponse> responses = new ArrayList<>();


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Double getGrade()
    {
        return this.grade;
    }

    public void setGrade(Double grade)
    {
        this.grade = grade;
    }

    public List<StudentResponse> getResponses() { return responses; }
    public void setResponses(List<StudentResponse> responses) { this.responses = responses; }
}

