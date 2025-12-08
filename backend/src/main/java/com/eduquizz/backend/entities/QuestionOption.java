package com.eduquizz.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;
    private String text;
    private Boolean isCorrect;
    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Boolean isCorrect() {
        return isCorrect;
    }
    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
