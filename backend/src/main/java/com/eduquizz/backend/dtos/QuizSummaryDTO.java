package com.eduquizz.backend.dtos;

import java.time.LocalDateTime;

public class QuizSummaryDTO {
    private Long quizId;
    private String name;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private Integer timeLimit;

    public QuizSummaryDTO(Long quizId, String name,  LocalDateTime activeFrom, LocalDateTime activeUntil, Integer timeLimit)
    {
        this.quizId = quizId;
        this.name = name;
        this.activeFrom = activeFrom;
        this.activeUntil = activeUntil;
        this.timeLimit = timeLimit;
    }

    public Long getQuizId()
    {
        return this.quizId;
    }

    public void setQuizId(Long quizId)
    {
        this.quizId = quizId;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDateTime getActiveFrom() { return activeFrom; }
    public LocalDateTime getActiveUntil() { return activeUntil; }
    public Integer getTimeLimit() { return timeLimit; }
}
