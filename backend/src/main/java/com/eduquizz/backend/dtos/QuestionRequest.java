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

    private String correctTextAnswer;
    private List<QuestionTestCaseRequest> questionTestCaseRequest;
    public QuestionRequest() {}

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

    public String getCorrectTextAnswer() {
        return correctTextAnswer;
    }

    public List<QuestionTestCaseRequest> getQuestionTestCaseRequest() {
        return questionTestCaseRequest;
    }
}
