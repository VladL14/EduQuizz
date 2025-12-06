package com.eduquizz.backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes")
public class Quiz
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classroom classroom;
    
    private String title;

    @Column(name = "active_from")
    private LocalDateTime activeFrom;
    
    @Column(name = "active_until")
    private LocalDateTime activeUntil;

    private Integer timeLimit;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttempt> attempts = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getActiveFrom() {
        return activeFrom;
    }
    public void setActiveFrom(LocalDateTime activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDateTime getActiveUntil() {
        return activeUntil;
    }
    public void setActiveUntil(LocalDateTime activeUntil) {
        this.activeUntil = activeUntil;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<QuizAttempt> getAttempts() { return attempts; }
    public void setAttempts(List<QuizAttempt> attempts) { this.attempts = attempts; }
}
