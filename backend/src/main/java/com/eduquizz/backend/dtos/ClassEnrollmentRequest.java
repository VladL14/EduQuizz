package com.eduquizz.backend.dtos;

public class ClassEnrollmentRequest {
    private Long studentId;
    private String code;

    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
