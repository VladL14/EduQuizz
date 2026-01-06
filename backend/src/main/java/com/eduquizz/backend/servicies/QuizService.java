package com.eduquizz.backend.servicies;




import org.springframework.stereotype.Service;

import com.eduquizz.backend.dtos.QuestionOptionRequest;
import com.eduquizz.backend.dtos.QuestionRequest;
import com.eduquizz.backend.dtos.QuestionTestCaseRequest;
import com.eduquizz.backend.dtos.QuizRequest;
import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.Question;
import com.eduquizz.backend.entities.QuestionOption;
import com.eduquizz.backend.entities.QuestionTestCase;
import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.QuestionOptionRepository;
import com.eduquizz.backend.repositories.QuestionRepository;
import com.eduquizz.backend.repositories.QuestionTestCaseRepository;
import com.eduquizz.backend.repositories.QuizRepository;
import com.eduquizz.backend.utils.RequestType;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {
    private final ClassroomRepository classroomRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionTestCaseRepository questionTestCaseRepository;

    public QuizService(ClassroomRepository classroomRepository, QuizRepository quizRepository, QuestionRepository questionRepository, QuestionOptionRepository questionOptionRepository, QuestionTestCaseRepository questionTestCaseRepository)
    {
        this.classroomRepository = classroomRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionTestCaseRepository = questionTestCaseRepository;
    }

    @Transactional
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

        quizRepository.save(quiz);

        for(QuestionRequest questionRequest : request.getQuestionRequest())
        {
            Question question = new Question();
            question.setQuiz(quiz);
            question.setPoints(questionRequest.getPoints());
            question.setText(questionRequest.getText());
            question.setType(questionRequest.getType());

            if(questionRequest.getType() == RequestType.TEXT)
            {
                question.setCorrectTextAnswer(questionRequest.getCorrectTextAnswer());
            }

            questionRepository.save(question);

            if(questionRequest.getType() == RequestType.GRID && questionRequest.getQuestionOptionRequest() != null)
            {
                for(QuestionOptionRequest questionOptionRequest : questionRequest.getQuestionOptionRequest())
                {
                    QuestionOption questionOption = new QuestionOption();
                    questionOption.setQuestion(question);
                    questionOption.setText(questionOptionRequest.getText());
                    questionOption.setIsCorrect(questionOptionRequest.isCorrect());
                    questionOptionRepository.save(questionOption);
                }
            }

            if(questionRequest.getType() == RequestType.CODE && questionRequest.getQuestionTestCaseRequest() != null)
            {
                for(QuestionTestCaseRequest questionTestCaseRequest : questionRequest.getQuestionTestCaseRequest())
                {
                    QuestionTestCase questionTestCase = new QuestionTestCase();
                    questionTestCase.setQuestion(question);
                    questionTestCase.setInput(questionTestCaseRequest.getInput());
                    questionTestCase.setExpectedOutput(questionTestCaseRequest.getExpectedOutput());
                    questionTestCaseRepository.save(questionTestCase);
                }
            }

        }

        return quiz;
    }

    public List<Quiz> getQuizzesByClassroom(Long classroomId) {
        List<Quiz> quizzes = quizRepository.findByClassroomId(classroomId);

        if(classroomRepository.findById(classroomId).isEmpty())
        {
            throw new RuntimeException("Classroom with id: " + classroomId + " not found");
        }

        return quizzes;
    }

    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }

        quizRepository.deleteById(id);
    }


    public Quiz updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        if(quiz.getActiveUntil().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("The quiz is already finished and cannot be updated.");
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            quiz.setTitle(request.getTitle());
        }

        if (request.getTimeLimit() != null) {
            if (request.getTimeLimit() <= 0) {
                throw new RuntimeException("Time limit must be higher than 0");
            }
            quiz.setTimeLimit(request.getTimeLimit());
        }

        LocalDateTime newFrom = request.getActiveFrom() != null ? request.getActiveFrom() : quiz.getActiveFrom();
        LocalDateTime newUntil = request.getActiveUntil() != null ? request.getActiveUntil() : quiz.getActiveUntil();

        if (newFrom.isAfter(newUntil)) {
            throw new RuntimeException("Active from must be before active until");
        }

        quiz.setActiveFrom(newFrom);
        quiz.setActiveUntil(newUntil);

        return quizRepository.save(quiz);
    }

}