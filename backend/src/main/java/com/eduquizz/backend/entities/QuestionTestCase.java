package com.eduquizz.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "question_test_cases")
public class QuestionTestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String stdin;

    @Column(name = "expected_stdout", columnDefinition = "TEXT")
    private String expectedStdout;

    @Column(name = "is_public")
    private Boolean isPublic;
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
    public String getStdin() {
        return stdin;
    }
    public void setStdin(String stdin) {
        this.stdin = stdin;
    }
    public String getExpectedStdout() {
        return expectedStdout;
    }
    public void setExpectedStdout(String expectedStdout) {
        this.expectedStdout = expectedStdout;
    }
    public Boolean getIsPublic() {
        return isPublic;
    }
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
}
