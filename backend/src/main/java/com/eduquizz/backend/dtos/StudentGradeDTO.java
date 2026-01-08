package com.eduquizz.backend.dtos;

import java.util.Map;

public class StudentGradeDTO {
    private Long studentId;
    private String username;
    private String email;
    private Map<Long, String> grades; 

    public StudentGradeDTO(Long studentId, String username, String email, Map<Long, String> grades) {
        this.studentId = studentId;
        this.username = username;
        this.email = email;
        this.grades = grades;
    }

    public Long getStudentId() { return studentId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Map<Long, String> getGrades() { return grades; }
}