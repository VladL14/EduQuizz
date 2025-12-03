package com.eduquizz.backend.entities;

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
    private Date activeFrom;
    private Date activeUntil;
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
    public Date getActiveFrom() {
        return activeFrom;
    }
    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }
    public Date getActiveUnitl() {
        return activeUntil;
    }
    public void setActiveUnitl(Date activeUnitl) {
        this.activeUntil = activeUnitl;
    }
    public Integer getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
}
