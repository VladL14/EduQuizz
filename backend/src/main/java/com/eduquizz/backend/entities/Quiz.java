package com.eduquizz.backend.entities;

import java.time.LocalDateTime;
import java.util.Date;

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
    
    @Column(name = "actitve_until")
    private LocalDateTime activeUntil;

    private Integer timeLimit;

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
    public LocalDateTime getActiveUnitl() {
        return activeUntil;
    }
    public void setActiveUnitl(LocalDateTime activeUnitl) {
        this.activeUntil = activeUnitl;
    }
    public Integer getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
}
