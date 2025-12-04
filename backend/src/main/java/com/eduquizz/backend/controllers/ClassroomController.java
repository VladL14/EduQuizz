package com.eduquizz.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.servicies.ClassroomService;

import lombok.Data;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @PostMapping("/create")
    public ResponseEntity<?> createClassroom(@RequestBody CreateClassroomRequest request)
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
    
}

@Data
class CreateClassroomRequest {
    private Long teacherId;
    private String className;
}
