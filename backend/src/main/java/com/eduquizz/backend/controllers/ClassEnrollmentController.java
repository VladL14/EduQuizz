package com.eduquizz.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.entities.ClassEnrollment;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.servicies.ClassEnrollmentService;
import com.eduquizz.backend.dtos.ClassEnrollmentRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/classEnrollments")
public class ClassEnrollmentController {
    @Autowired
    private ClassEnrollmentService classEnrollmentService;

    @PutMapping("/enrollStudent")
    public ResponseEntity<?> enrollStudent(@RequestBody ClassEnrollmentRequest request)
    {
        try
        {
            ClassEnrollment classEnrollment = classEnrollmentService.enrollStudent(
                request.getStudentId(),
                request.getCode()
            );
            return ResponseEntity.ok(classEnrollment);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/classrooms/{studentId}")
    public ResponseEntity<?> getClassroomsByStudentId(Long studentId)
    {
        try
        {
            List<Classroom> classEnrollments = classEnrollmentService.getClassroomsByStudentId(studentId);
            return ResponseEntity.ok(classEnrollments);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
