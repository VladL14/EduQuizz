package com.eduquizz.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduquizz.backend.entities.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{ 

}
