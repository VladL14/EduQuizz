package com.eduquizz.backend.dtos;

import java.time.LocalDateTime;

import com.eduquizz.backend.utils.AttemptStatus;

public class QuizSummaryDTO {
    private Long id;
    private String title;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private Integer timeLimit;

    private String status;
    private Double grade;

    public QuizSummaryDTO(Long id, String title,  LocalDateTime activeFrom, LocalDateTime activeUntil, Integer timeLimit, String status, Double grade)
    {
        this.id = id;
        this.title = title;
        this.activeFrom = activeFrom;
        this.activeUntil = activeUntil;
        this.timeLimit = timeLimit;
        this.status = status;
        this.grade = grade;
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public LocalDateTime getActiveFrom() { return activeFrom; }
    public LocalDateTime getActiveUntil() { return activeUntil; }
    public Integer getTimeLimit() { return timeLimit; }

    public String getStatus() { return status; }
    public Double getGrade() { return grade; }
}
