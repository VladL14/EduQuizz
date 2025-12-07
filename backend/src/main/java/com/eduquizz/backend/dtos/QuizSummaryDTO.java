package com.eduquizz.backend.dtos;

public class QuizSummaryDTO {
    private Long quizId;
    private String name;

    public QuizSummaryDTO(Long quizId, String name)
    {
        this.quizId = quizId;
        this.name = name;
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
}
