package com.eduquizz.backend.utils;

import com.eduquizz.backend.entities.*;
import com.eduquizz.backend.repositories.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    // Injectăm repository-urile de care avem nevoie
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private ClassEnrollmentRepository classEnrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Verificam daca baza de date e goala ca sa nu duplicam datele la fiecare restart
        if (userRepository.count() == 0) {
            
            // 1. Creăm un Profesor
            User prof = new User();
            prof.setUsername("sebastian_stefaniga");
            prof.setEmail("sebastian.stefaniga@e-uvt.ro");
            prof.setPassword(passwordEncoder.encode("1234"));
            prof.setRole(RequestRole.TEACHER); 
            
            userRepository.save(prof); // Salvam în DB

            // 2. Cream 3 Studenti
            User s1 = new User();
            s1.setUsername("alex_adrian_ilie");
            s1.setEmail("alexandru.ilie04@e-uvt.ro");
            s1.setPassword(passwordEncoder.encode("1234"));
            s1.setRole(RequestRole.STUDENT);
            
            User s2 = new User();
            s2.setUsername("vlad_lunculescu");
            s2.setEmail("gheorghe.lunculescu04@e-uvt.ro");
            s2.setPassword(passwordEncoder.encode("1234"));
            s2.setRole(RequestRole.STUDENT);

            User s3 = new User();
            s3.setUsername("alex_cirlugea");
            s3.setEmail("alexandru.cirlugea04@e-uvt.ro");
            s3.setPassword(passwordEncoder.encode("1234"));
            s3.setRole(RequestRole.STUDENT);
            userRepository.saveAll(List.of(s1, s2, s3));

            // 3. Cream o Clasa si o legam de Profesor
            Classroom clasaWeb = new Classroom();
            clasaWeb.setClassName("Tehnologii Web");
            clasaWeb.setEnrollmentCode("web123");
            clasaWeb.setTeacher(prof);
            
            classroomRepository.save(clasaWeb);

            ClassEnrollment classEnrollment = new ClassEnrollment();
            classEnrollment.setClassroom(clasaWeb);
            classEnrollment.setStudent(s1);
            
            ClassEnrollment classEnrollment1 = new ClassEnrollment();
            classEnrollment1.setClassroom(clasaWeb);
            classEnrollment1.setStudent(s2);

            ClassEnrollment classEnrollment3 = new ClassEnrollment();
            classEnrollment3.setClassroom(clasaWeb);
            classEnrollment3.setStudent(s3);

            classEnrollmentRepository.saveAll(List.of(classEnrollment, classEnrollment1, classEnrollment3));

            Quiz quiz = new Quiz();
            quiz.setTitle("Etapa 1 - Alegere proiecte");
            quiz.setActiveFrom(LocalDateTime.of(2025, 10, 10, 12, 0));
            quiz.setActiveUntil(LocalDateTime.of(2025, 10, 10, 23, 59));
            quiz.setTimeLimit(15);
            quiz.setClassroom(clasaWeb);

            quizRepository.save(quiz);

            Quiz quiz2 = new Quiz();
            quiz2.setTitle("Etapa 2 - Pitching Day");
            quiz2.setActiveFrom(LocalDateTime.of(2025, 10, 26, 12, 0));
            quiz2.setActiveUntil(LocalDateTime.of(2025, 11, 4, 23, 59));
            quiz2.setTimeLimit(60);
            quiz2.setClassroom(clasaWeb);

            quizRepository.save(quiz2);

            Quiz quiz3 = new Quiz();
            quiz3.setTitle("Etapa 3 - Proiect Intermediar");
            quiz3.setActiveFrom(LocalDateTime.now().minusDays(1).withHour(14).withMinute(0));
            quiz3.setActiveUntil(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
            quiz3.setTimeLimit(60);
            quiz3.setClassroom(clasaWeb);

            quizRepository.save(quiz3);

            Quiz quiz4 = new Quiz();
            quiz4.setTitle("Etapa 4 - Proiect Final");
            quiz4.setActiveFrom(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0));
            quiz4.setActiveUntil(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0));
            quiz4.setTimeLimit(60);
            quiz4.setClassroom(clasaWeb);

            quizRepository.save(quiz4);


            QuizAttempt attempt = new QuizAttempt();
            attempt.setStudent(s1);
            attempt.setQuiz(quiz);
            attempt.setStartTime(LocalDateTime.now().minusHours(2));
            attempt.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt);

            QuizAttempt attempt1 = new QuizAttempt();
            attempt1.setStudent(s1);
            attempt1.setQuiz(quiz2);
            attempt1.setStartTime(LocalDateTime.now().minusHours(2));
            attempt1.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt1.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt1);

            QuizAttempt attempt2 = new QuizAttempt();
            attempt2.setStudent(s2);
            attempt2.setQuiz(quiz);
            attempt2.setStartTime(LocalDateTime.now().minusHours(2));
            attempt2.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt2.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt2);


            QuizAttempt attempt3 = new QuizAttempt();
            attempt3.setStudent(s2);
            attempt3.setQuiz(quiz2);
            attempt3.setStartTime(LocalDateTime.now().minusHours(2));
            attempt3.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt3.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt3);

            QuizAttempt attempt4 = new QuizAttempt();
            attempt4.setStudent(s3);
            attempt4.setQuiz(quiz);
            attempt4.setStartTime(LocalDateTime.now().minusHours(2));
            attempt4.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt4.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt4);


            QuizAttempt attempt5 = new QuizAttempt();
            attempt5.setStudent(s3);
            attempt5.setQuiz(quiz2);
            attempt5.setStartTime(LocalDateTime.now().minusHours(2));
            attempt5.setStatus(AttemptStatus.COMPLETED); // 1 = COMPLETED
            attempt5.setGrade(10);  // NOTA 10
            quizAttemptRepository.save(attempt5);


            System.out.println("Baza de date a fost populata cu succes!");
        }
    }
}