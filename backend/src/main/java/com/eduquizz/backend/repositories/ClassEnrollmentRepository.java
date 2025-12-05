package com.eduquizz.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eduquizz.backend.entities.ClassEnrollment;
@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long>{
    boolean existsByStudentIdAndClassroomId(Long studentId, Long classroomId);

    List<ClassEnrollment> findAllByStudentId(Long studentId);
}
