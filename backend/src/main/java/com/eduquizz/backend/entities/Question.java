package com.eduquizz.backend.entities;

import com.eduquizz.backend.utils.RequestType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz;

    private String text;
    
    @Enumerated(EnumType.STRING)
    private RequestType type;

    private Integer points;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionTestCase> testCases = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentResponse> responses = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Quiz getQuiz() {
        return quiz;
    }
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public RequestType getType() {
        return type;
    }
    public void setType(RequestType type) {
        this.type = type;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }

    public List<QuestionOption> getOptions() { return options; }
    public void setOptions(List<QuestionOption> options) { this.options = options; }

    public List<QuestionTestCase> getTestCases() { return testCases; }
    public void setTestCases(List<QuestionTestCase> testCases) { this.testCases = testCases; }

    public List<StudentResponse> getResponses() { return responses; }
    public void setResponses(List<StudentResponse> responses) { this.responses = responses; }
}
