package com.eduquizz.backend.servicies;




import com.eduquizz.backend.dtos.*;
import com.eduquizz.backend.entities.*;
import com.eduquizz.backend.repositories.*;
import com.eduquizz.backend.utils.AttemptStatus;
import org.springframework.stereotype.Service;

import com.eduquizz.backend.utils.RequestType;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final ClassroomRepository classroomRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionTestCaseRepository questionTestCaseRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizService(ClassroomRepository classroomRepository, QuizRepository quizRepository, QuestionRepository questionRepository, QuestionOptionRepository questionOptionRepository, QuestionTestCaseRepository questionTestCaseRepository, QuizAttemptRepository quizAttemptRepository, StudentResponseRepository studentResponseRepository)
    {
        this.classroomRepository = classroomRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionTestCaseRepository = questionTestCaseRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
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

    public List<QuizSummaryDTO> getQuizSummariesByClassroom(Long classroomId, Long studentId) {
        List<Quiz> quizzes = quizRepository.findByClassroomId(classroomId);
        List<QuizSummaryDTO> quizDTOs = new ArrayList<>();

        for (Quiz q : quizzes) {

            Optional<QuizAttempt> attempt = quizAttemptRepository.findByStudentIdAndQuizId(studentId, q.getId());

            String status = "NEW";
            Double grade = null;

            if(attempt.isPresent())
            {
                status = attempt.get().getStatus().toString();
                grade = attempt.get().getGrade();

            }
            else
            {
                if(q.getActiveUntil() != null && q.getActiveUntil().isBefore(LocalDateTime.now()))
                {
                    status = "EXPIRED";
                }
            }

            QuizSummaryDTO dto = new QuizSummaryDTO(
                    q.getId(),
                    q.getTitle(),
                    q.getActiveFrom(),
                    q.getActiveUntil(),
                    q.getTimeLimit(),
                    status,
                    grade
            );
            quizDTOs.add(dto);
        }

        return quizDTOs;
    }

    public QuizAttempt getAttemptByStudentAndQuiz(Long studentId, Long quizId) {
        return quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found for student id: " + studentId + " and quiz id: " + quizId));
    }

}