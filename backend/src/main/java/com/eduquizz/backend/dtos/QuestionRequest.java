package com.eduquizz.backend.dtos;

import java.util.List;

import com.eduquizz.backend.entities.QuestionOption;
import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.utils.RequestType;

public class QuestionRequest {
    private Integer points;
    private String text;
    private RequestType type;

    private List<QuestionOptionRequest> questionOptionRequest;



    public QuestionRequest(
        Integer points,
        String text,
        RequestType type,
        List<QuestionOptionRequest> request
    ){
        this.points = points;
        this.text = text;
        this.type = type;
        this.questionOptionRequest = request;
    }

    public Integer getPoints() {
        return points;
    }

    public String getText() {
        return text;
    }

    public RequestType getType() {
        return type;
    }

    public List<QuestionOptionRequest> getQuestionOptionRequest()
    {
        return this.questionOptionRequest;
    }
}
