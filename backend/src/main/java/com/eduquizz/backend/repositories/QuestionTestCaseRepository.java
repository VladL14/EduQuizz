package com.eduquizz.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduquizz.backend.entities.QuestionTestCase;

@Repository
public interface QuestionTestCaseRepository extends JpaRepository<QuestionTestCase, Long>{

}
