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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Verificam daca baza de date e goala ca sa nu duplicam datele la fiecare restart
        if (userRepository.count() == 0) {
            
            // 1. Creăm un Profesor
            User prof = new User();
            prof.setUsername("profesor_vlad");
            prof.setEmail("vlad@edu.com");
            prof.setPassword(passwordEncoder.encode("parola123"));
            prof.setRole(RequestRole.TEACHER); 
            
            userRepository.save(prof); // Salvam în DB

            // 2. Cream 2 Studenti
            User s1 = new User();
            s1.setUsername("student_ion");
            s1.setEmail("ion@stud.com");
            s1.setPassword(passwordEncoder.encode("1234"));
            s1.setRole(RequestRole.STUDENT);
            
            User s2 = new User();
            s2.setUsername("student_maria");
            s2.setEmail("maria@stud.com");
            s2.setPassword(passwordEncoder.encode("abcd"));
            s2.setRole(RequestRole.STUDENT);

            userRepository.saveAll(List.of(s1, s2));

            // 3. Cream o Clasa si o legam de Profesor
            Classroom clasaJava = new Classroom();
            clasaJava.setClassName("Curs Java Spring Boot");
            clasaJava.setEnrollmentCode("JAVA2024");
            clasaJava.setTeacher(prof);
            
            classroomRepository.save(clasaJava);
            
            Quiz quiz = new Quiz();
            quiz.setTitle("Test Java");
            quiz.setActiveFrom(LocalDateTime.now().withHour(14).withMinute(0));
            quiz.setActiveUntil(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0));
            quiz.setTimeLimit(60);
            quiz.setClassroom(clasaJava);

            quizRepository.save(quiz);

            System.out.println("Baza de date a fost populata cu succes!");
        }
    }
}