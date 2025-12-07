package com.eduquizz.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.dtos.ClassroomRequest;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.servicies.ClassroomService;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @PostMapping("/create")
    public ResponseEntity<?> createClassroom(@RequestBody ClassroomRequest request)
    {
        try {
            Classroom newClassroom = classroomService.createClassroom(
                request.getTeacherId(),
                request.getClassName()
            );
            return ResponseEntity.ok(newClassroom);
        } catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassroomsById(@PathVariable Long id) 
    {
        try
        {
            Classroom classroom = classroomService.getClassroomById(id);
            return ResponseEntity.ok(classroom);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getClassroomsByTeacher(@PathVariable Long teacherId)
    {
        try
        {
            List<Classroom> classrooms = classroomService.getClassroomsByTeacherId(teacherId);
            return ResponseEntity.ok(classrooms);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
