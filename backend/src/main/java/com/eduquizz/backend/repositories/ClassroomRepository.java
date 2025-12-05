package com.eduquizz.backend.repositories;

import com.eduquizz.backend.entities.Classroom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByEnrollmentCode(String enrollmentCode);
    List<Classroom> findAllByTeacherId(Long teacherId);
}