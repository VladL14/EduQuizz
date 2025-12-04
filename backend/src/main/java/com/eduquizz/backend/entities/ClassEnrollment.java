package com.eduquizz.backend.entities;

import jakarta.persistence.*;
@Entity
@Table(name = "class_enrollments")
public class ClassEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classroom classroom;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public User getStudent() {
        return student;
    }
    public void setStudent(User student2) {
        this.student = student2;
    }
}