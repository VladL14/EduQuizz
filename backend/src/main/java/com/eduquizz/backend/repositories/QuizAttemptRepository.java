package com.eduquizz.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduquizz.backend.entities.QuizAttempt;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long>{

    List<QuizAttempt> findByQuizClassroomId(Long classroomId);
    Optional<QuizAttempt> findByStudentIdAndQuizId(Long studentId, Long quizId); 

}
