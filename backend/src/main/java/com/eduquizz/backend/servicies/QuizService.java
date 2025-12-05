package com.eduquizz.backend.servicies;

import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.repositories.ClassroomRepository;

@Service
public class QuizService {
    private final ClassroomRepository classroomRepository;

    public QuizService(ClassroomRepository classroomRepository)
    {
        this.classroomRepository = classroomRepository;
    }
}
