package com.eduquizz.backend.dtos;

import java.time.LocalDateTime;

public class QuizRequest {
    private String title;
    private Long classroomId;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private Integer timeLimit;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public LocalDateTime getActiveFrom() { return activeFrom; }
    public void setActiveFrom(LocalDateTime activeFrom) { this.activeFrom = activeFrom; }

    public LocalDateTime getActiveUntil() { return activeUntil; }
    public void setActiveUntil(LocalDateTime activeUntil) { this.activeUntil = activeUntil; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
}
