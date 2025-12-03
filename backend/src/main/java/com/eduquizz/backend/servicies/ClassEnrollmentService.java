package com.eduquizz.backend.servicies;

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
    @Autowired
    private ClassEnrollmentRepository classEnrollmentRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

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

    public List<ClassEnrollment> getStudentEnrollments(Long studentId)
    {
        return classEnrollmentRepository.findByStudentId(studentId);
    }
}
