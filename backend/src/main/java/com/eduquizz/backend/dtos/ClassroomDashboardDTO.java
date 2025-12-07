package com.eduquizz.backend.dtos;

import java.util.List;
import java.util.Map;

import com.eduquizz.backend.entities.Quiz;

public class ClassroomDashboardDTO {
    private Long classroomId;
    private String classroomName;
    private String code;
    private List<QuizSummaryDTO> quizzes;
    private List<StudentGradeDTO> students;

    public ClassroomDashboardDTO(
        Long classroomId,
        String classroomName,
        String code,
        List<QuizSummaryDTO> quizzes,
        List<StudentGradeDTO> students
    ){
        this.classroomId = classroomId;
        this.classroomName = classroomName;
        this.code = code;
        this.quizzes = quizzes;
        this.students = students;
    }

    public Long getClassroomId()
    {
        return this.classroomId;
    }

    public String getClassroomName()
    {
        return this.classroomName;
    }

    public String getCode()
    {
        return this.code;
    }

    public List<QuizSummaryDTO> getQuizzes()
    {
        return this.quizzes;
    }

    public List<StudentGradeDTO> getStudents()
    {
        return this.students;
    }
}
