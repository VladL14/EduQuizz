package com.eduquizz.backend.servicies;




import org.springframework.stereotype.Service;
import com.eduquizz.backend.dtos.QuizRequest;
import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.QuizRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {
    private final ClassroomRepository classroomRepository;
    private final QuizRepository quizRepository;

    public QuizService(ClassroomRepository classroomRepository, QuizRepository quizRepository)
    {
        this.classroomRepository = classroomRepository;
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(QuizRequest request) {
        Integer timeLimit = request.getTimeLimit();
        LocalDateTime activeFrom = request.getActiveFrom();
        LocalDateTime activeUntil = request.getActiveUntil();

        if (timeLimit == null || timeLimit <= 0) {
            throw new RuntimeException("Time limit must be higher than 0");
        }

        if (activeFrom == null || activeUntil == null) {
            throw new RuntimeException("Both start and end dates are required");
        }
        if (activeFrom.isAfter(activeUntil)) {
            throw new RuntimeException("Active from must be before active until");
        }
        if (activeFrom.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Quiz cannot be scheduled in the past");
        }

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + request.getClassroomId()));

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setClassroom(classroom);
        quiz.setActiveFrom(request.getActiveFrom());
        quiz.setActiveUntil(request.getActiveUntil());
        quiz.setTimeLimit(request.getTimeLimit());

        return quizRepository.save(quiz);
    }

    public List<Quiz> getQuizzesByClassroom(Long classroomId) {
        return quizRepository.findByClassroomId(classroomId);
    }

}
