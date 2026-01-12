package com.eduquizz.backend.servicies;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.eduquizz.backend.dtos.SubmitQuizRequest;
import com.eduquizz.backend.entities.Question;
import com.eduquizz.backend.entities.QuestionOption;
import com.eduquizz.backend.entities.QuestionTestCase;
import com.eduquizz.backend.entities.Quiz;
import com.eduquizz.backend.entities.QuizAttempt;
import com.eduquizz.backend.entities.StudentResponse;
import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.QuestionRepository;
import com.eduquizz.backend.repositories.QuizAttemptRepository;
import com.eduquizz.backend.repositories.QuizRepository;
import com.eduquizz.backend.repositories.StudentResponseRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.AttemptStatus;
import com.eduquizz.backend.utils.RequestRole;
import com.eduquizz.backend.utils.RequestType;
import com.eduquizz.backend.servicies.PythonJudgeService;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {
    @Autowired
    private PythonJudgeService pythonJudgeService;

    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final StudentResponseRepository studentResponseRepository;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, UserRepository userRepository, QuizRepository quizRepository, QuestionRepository questionRepository, StudentResponseRepository studentResponseRepository)
    {
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.studentResponseRepository = studentResponseRepository;
    }

    public QuizAttempt getQuizAttemptByStudentIdAndQuizId(Long studentId, Long quizId)
    {
        User user = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("User with id: " + studentId + " not found."));

        return quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
            .orElseThrow(() -> new RuntimeException("The student hasn't started the test yet."));
    }

    public List<QuizAttempt> getStudentAttemptsByClassroom(Long studentId, Long classroomId) {
        return quizAttemptRepository.findByStudentIdAndQuizClassroomId(studentId, classroomId);
    }

    @Transactional
    public QuizAttempt startQuiz(Long studentId, Long quizId)
    {
        Optional<QuizAttempt> quizAttemptOptional = quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId);

        if(quizAttemptOptional.isPresent())
        {
            if(quizAttemptOptional.get().getStatus().equals(AttemptStatus.IN_PROGRESS))
            {
                return quizAttemptOptional.get();
            }

            if(quizAttemptOptional.get().getStatus().equals(AttemptStatus.COMPLETED) || quizAttemptOptional.get().getStatus().equals(AttemptStatus.PENDING_REVIEW))
            {
                throw new RuntimeException("Quiz cannot be started beacuse it is already completed or in pending review status");
            }
        }

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz with id: " + quizId + " not found."));
        User user = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("User with id: " + studentId + " not found"));


        if(quiz.getActiveFrom().isAfter(LocalDateTime.now()))
        {
            throw new RuntimeException("Quiz cannot be started before its active from date");
        }

        if(quiz.getActiveUntil().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("Quiz cannot be started after its active to date");
        }

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setQuiz(quiz);
        quizAttempt.setStudent(user);
        quizAttempt.setStartTime(LocalDateTime.now());
        quizAttempt.setGrade(0.0);
        quizAttempt.setStatus(AttemptStatus.IN_PROGRESS);

        return quizAttemptRepository.save(quizAttempt);
    }

    @Transactional
    public QuizAttempt submitQuiz(Long studentId, Long quizId, List<SubmitQuizRequest> submitQuizRequests)
    {
        QuizAttempt quizAttempt = quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId).orElseThrow(() -> new RuntimeException("A started quiz doesn't exist"));
        if(quizAttempt.getStatus().equals(AttemptStatus.IN_PROGRESS))
        {
            double totalScore = 0.0;
            boolean needsManualGrading = false;

            for(SubmitQuizRequest answerRequest : submitQuizRequests)
            {
                Question question = questionRepository.findById(answerRequest.getQuestionId()).orElseThrow(() -> new RuntimeException("Question not found"));
                StudentResponse response = new StudentResponse();
                response.setQuestion(question);
                response.setQuizAttempt(quizAttempt);

                boolean isAnswerCorrect = false;

                if(question.getType() == RequestType.CODE)
                {
                    response.setTextAnswer(answerRequest.getTextAnswer());

                    List<QuestionTestCase> testCases = question.getTestCases();
                    if(testCases != null && !testCases.isEmpty())
                    {
                        boolean passedAll = true;
                        for(QuestionTestCase testCase : testCases)
                        {
                            var result = pythonJudgeService.executePythonCode(
                                answerRequest.getTextAnswer(),
                                testCase.getInput(),
                                testCase.getExpectedOutput()
                            );
                            if(!result.success())
                            {
                                passedAll = false;
                                break;
                            }
                        }
                        isAnswerCorrect = passedAll;
                    }
                }
                else if(question.getType() == RequestType.GRID)
                {
                    Set<Long> correctOptionIds = question.getOptions().stream()
                            .filter(QuestionOption::isCorrect)
                            .map(QuestionOption::getId)
                            .collect(Collectors.toSet());

                    List<Long> selectedIds = answerRequest.getSelectedOptionIds();
                    Set<Long> studentOptionIds = (selectedIds == null) ? new HashSet<>() : new HashSet<>(selectedIds);

                    if(correctOptionIds.equals(studentOptionIds))
                    {
                        isAnswerCorrect = true;
                    }
                }
                else if(question.getType() == RequestType.TEXT)
                {
                    response.setTextAnswer(answerRequest.getTextAnswer());
                    isAnswerCorrect = false;
                    needsManualGrading = true;
                }

                response.setCorrect(isAnswerCorrect);
                    
                if(isAnswerCorrect)
                {
                     totalScore += question.getPoints();
                }
                studentResponseRepository.save(response);
            }

                quizAttempt.setGrade(totalScore);
                quizAttempt.setStatus(needsManualGrading ? AttemptStatus.PENDING_REVIEW : AttemptStatus.COMPLETED);

                return quizAttemptRepository.save(quizAttempt);

        }
        throw new RuntimeException("Quiz is already completed or it is waiting review");
    }

    @Transactional
    public QuizAttempt gradeQuizAttempt(Long quizAttemptId, Long questionId, double newGrade)
    {
        QuizAttempt quizAttempt = quizAttemptRepository.findById(quizAttemptId)
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + quizAttemptId));
        
        StudentResponse response = quizAttempt.getResponses().stream()
            .filter(r -> r.getQuestion().getId().equals(questionId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Response not found for question id: " + questionId));

            if(newGrade > response.getQuestion().getPoints())
            {
                throw new RuntimeException("New grade cannot be higher than the question's maximum points");
            }

            if(response.getQuestion().getType() == RequestType.GRID)
            {
                throw new RuntimeException("Only TEXT and CODE type questions can be graded manually");
            }
            response.setCorrect(newGrade == response.getQuestion().getPoints());
            response.setScore(newGrade);
            studentResponseRepository.save(response);

            double totalScore = quizAttempt.getResponses().stream()
                .mapToDouble(StudentResponse::getScore)
                .sum();

            quizAttempt.setGrade(totalScore);
            quizAttempt.setStatus(AttemptStatus.COMPLETED);
            return quizAttemptRepository.save(quizAttempt);

        }

        public QuizAttempt getAttemptById(Long id) {
            return quizAttemptRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + id));
        }

        public QuizAttempt getAttemptByStudentAndQuiz(Long studentId, Long quizId) {
            return quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz attempt not found for student id: " + studentId + " and quiz id: " + quizId));
        }
    }