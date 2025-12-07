package com.eduquizz.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import com.eduquizz.backend.entities.ClassEnrollment;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.servicies.ClassEnrollmentService;
import com.eduquizz.backend.dtos.ClassEnrollmentRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/classEnrollments")
public class ClassEnrollmentController {
    @Autowired
    private ClassEnrollmentService classEnrollmentService;

    @PostMapping("/enrollStudent")
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
    public ResponseEntity<?> getClassroomsByStudentId(@PathVariable Long studentId)
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

    @GetMapping("/students/{classroomId}")
    public ResponseEntity<?> getStudentsByClassroomId(@PathVariable Long classroomId)
    {
        try
        {
            List<User> students = classEnrollmentService.getStudentsByClassroomId(classroomId);
            return ResponseEntity.ok(students);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
