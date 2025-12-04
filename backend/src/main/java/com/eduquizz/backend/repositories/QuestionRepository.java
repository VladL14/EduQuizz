package com.eduquizz.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eduquizz.backend.entities.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{

}
