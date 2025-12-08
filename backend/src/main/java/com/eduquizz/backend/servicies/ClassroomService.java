package com.eduquizz.backend.servicies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.eduquizz.backend.dtos.ClassroomDashboardDTO;
import com.eduquizz.backend.dtos.QuizSummaryDTO;
import com.eduquizz.backend.dtos.StudentGradeDTO;
import com.eduquizz.backend.entities.ClassEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.entities.QuizAttempt;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.ClassEnrollmentRepository;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.QuizAttemptRepository;
import com.eduquizz.backend.repositories.QuizRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

@Service
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public ClassroomService(ClassroomRepository classroomRepository, UserRepository userRepository, QuizRepository quizRepository, ClassEnrollmentRepository classEnrollmentRepository, QuizAttemptRepository quizAttemptRepository) {
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.quizAttemptRepository = quizAttemptRepository;
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
        Optional<Classroom> classroomOpt = classroomRepository.findById(id);
        if(classroomOpt.isEmpty())
        {
            throw new RuntimeException("Classroom not found with id: " + id);
        }

        classroomRepository.delete(classroomOpt.get());
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

    public ClassroomDashboardDTO getClassroomDashboard(Long classroomId, Long teacherId)
    {
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new RuntimeException("Classroom with id: " + classroomId + " not found."));
        
        if(!classroom.getTeacher().getId().equals(teacherId))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with id: " + teacherId + " is not a TEACHER");
        }

        List<Quiz> quizzes = quizRepository.findByClassroomId(classroomId);
        List<QuizSummaryDTO> quizDTOs = quizzes.stream()
                .map(q -> new QuizSummaryDTO(q.getId(), q.getTitle(), q.getActiveFrom(), q.getActiveUntil(), q.getTimeLimit()))
                .collect(Collectors.toList());

        List<ClassEnrollment> classEnrollments = classEnrollmentRepository.findAllByClassroomId(classroomId);
        List<User> students = classEnrollments.stream().map(ClassEnrollment::getStudent).collect(Collectors.toList());
        
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizClassroomId(classroomId);

        List<StudentGradeDTO> studentGrades = new ArrayList<>();

        for(User student : students)
        {
            Map<Long, Integer> grades = new HashMap<>();
            
            for(QuizAttempt attempt : attempts)
            {
                if(attempt.getStudent().getId().equals(student.getId()) && attempt.getGrade() != null)
                {
                    grades.put(attempt.getQuiz().getId(), attempt.getGrade());
                }
            }

            studentGrades.add(new StudentGradeDTO(
                    student.getId(),
                    student.getUsername(),
                    student.getEmail(),
                    grades
                ));
        }
        return new ClassroomDashboardDTO(
            classroom.getId(),
            classroom.getClassName(),
            classroom.getEnrollmentCode(),
            quizDTOs,
            studentGrades
        );
    }
}
