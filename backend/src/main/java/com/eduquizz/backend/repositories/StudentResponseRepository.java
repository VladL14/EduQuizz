package com.eduquizz.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduquizz.backend.entities.StudentResponse;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, Long>{

}
