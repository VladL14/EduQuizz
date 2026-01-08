package com.eduquizz.backend.repositories;

import com.eduquizz.backend.entities.Question;
import com.eduquizz.backend.entities.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduquizz.backend.entities.StudentResponse;

import java.util.Optional;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, Long>{
}
