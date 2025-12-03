package com.eduquizz.backend.servicies;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Classroom> getAllClassrooms()
    {
        return classroomRepository.findAll();
    }

    public Classroom createClassroom(Long teacherId, String className)
    {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + teacherId));
        if(teacher.getRole() != RequestRole.TEACHER)
        {
            throw new RuntimeException("Only teachers can create classrooms.");
        }
        Classroom classroom = new Classroom();
        classroom.setClassName(className);
        classroom.setTeacher(teacher);
        
        String randomCode = UUID.randomUUID().toString().substring(0, 6);
        classroom.setEnrollmentCode(randomCode);
        
        return classroomRepository.save(classroom);
    }

    public Optional<Classroom> getAllClassroomById(Long id)
    {
        return classroomRepository.findById(id);
    }
}
