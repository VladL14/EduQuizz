package com.eduquizz.backend.servicies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduquizz.backend.entities.ClassEnrollment;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.ClassEnrollmentRepository;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

@Service
public class ClassEnrollmentService {
    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    public ClassEnrollmentService(ClassEnrollmentRepository classEnrollmentRepository, ClassroomRepository classroomRepository, UserRepository userRepository) {
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
    }

    public ClassEnrollment enrollStudent(Long studentId, String code)
    {
        Classroom classroom = classroomRepository.findByEnrollmentCode(code).orElseThrow(() -> new RuntimeException("Classroom not found with code: " + code));
        User user = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("User not found with id: " + studentId));
        
        if(user.getRole() != RequestRole.STUDENT)
        {
            throw new RuntimeException("Only students can enroll in a classroom.");
        }
        if(classEnrollmentRepository.existsByStudentIdAndClassroomId(studentId, classroom.getId()))
        {
            throw new RuntimeException("Student is already enrolled in this classroom.");
        }
        ClassEnrollment classEnrollment = new ClassEnrollment();
        classEnrollment.setStudent(user);
        classEnrollment.setClassroom(classroom);
        return classEnrollmentRepository.save(classEnrollment);
    }

    public List<Classroom> getClassroomsByStudentId(Long studentId)
    {
        Optional<User> userOpt = userRepository.findById(studentId);
        if(userOpt.isEmpty())
        {
            throw new RuntimeException("User not found with id: " + studentId);
        }

        if(userOpt.get().getRole() != RequestRole.STUDENT)
        {
            throw new RuntimeException("User with id: " + studentId + " is not a STUDENT");
        }

        List<ClassEnrollment> classEnrollments = classEnrollmentRepository.findAllByStudentId(studentId);
        List<Classroom> classrooms = new ArrayList<>();
        for(ClassEnrollment classEnrollment : classEnrollments)
        {
            Classroom classroom = classEnrollment.getClassroom();
            classrooms.add(classroom);
        }

        return classrooms;
    }
}
