package com.eduquizz.backend.dtos;

public class QuestionTestCaseRequest {
    private String input;
    private String expectedOutput;

    // Constructors
    public QuestionTestCaseRequest() {}
    
    public QuestionTestCaseRequest(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    // Getters and Setters
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
}