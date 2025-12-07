package com.eduquizz.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduquizz.backend.dtos.ClassroomDashboardDTO;
import com.eduquizz.backend.dtos.ClassroomRequest;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.servicies.ClassroomService;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClasroomById(@PathVariable Long id)
    {
        try
        {
            classroomService.deleteClassroom(id);
            return ResponseEntity.ok("Classroom deleted successfully.");
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{classroomId}/dashboard")
    public ResponseEntity<?> getClassroomDashboard(@PathVariable Long classroomId, @RequestParam Long teacherId)
    {
        try
        {
            ClassroomDashboardDTO dashboard = classroomService.getClassroomDashboard(classroomId, teacherId);
            return ResponseEntity.ok(dashboard);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
