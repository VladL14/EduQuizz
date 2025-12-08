package com.eduquizz.backend.servicies;

import javax.management.RuntimeErrorException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.eduquizz.backend.entities.QuizAttempt;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.QuizAttemptRepository;
import com.eduquizz.backend.repositories.QuizRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

@Service
public class QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, UserRepository userRepository, QuizRepository quizRepository)
    {
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
    }

    public QuizAttempt getQuizAttemptByStudentIdAndQuizId(Long studentId, Long quizId)
    {
        User user = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("User with id: " + studentId + " not found."));

        return quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
            .orElseThrow(() -> new RuntimeException("The student hasn't started the test yet."));
    }
}
