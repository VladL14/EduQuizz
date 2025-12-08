package com.eduquizz.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionOptionRequest {
    @JsonProperty("isCorrect")
    private boolean isCorrect;
    private String text;

    public QuestionOptionRequest(boolean isCorrect, String text) {
        this.isCorrect = isCorrect;
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getText() {
        return text;
    }
}
