package com.eduquizz.backend.servicies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.eduquizz.backend.entities.ClassEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

@Service
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    public ClassroomService(ClassroomRepository classroomRepository, UserRepository userRepository) {
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
    }

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

    public Classroom getClassroomById(Long id)
    {
        return classroomRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
    }

    public void deleteClassroom(Long id)
    {
        Optional<Classroom> classroom = classroomRepository.findById(id);
        if(classroom.isEmpty())
        {
            throw new RuntimeException("Classroom not found with id: " + id);
        }

    }

    public List<Classroom> getClassroomsByTeacherId(Long teacherId)
    {
        Optional<User> userOpt = userRepository.findById(teacherId);
        if(userOpt.isEmpty())
        {
            throw new RuntimeException("User not found with id: " + teacherId);
        }

        if(userOpt.get().getRole() != RequestRole.TEACHER)
        {
            throw new RuntimeException("User with id: " + teacherId + " is not a TEACHER");
        }

        List<Classroom> classrooms = classroomRepository.findAllByTeacherId(teacherId);

        return classrooms;
    }
}
