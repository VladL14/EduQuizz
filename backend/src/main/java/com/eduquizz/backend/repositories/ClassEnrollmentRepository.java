package com.eduquizz.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.eduquizz.backend.entities.Classroom;
import com.eduquizz.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eduquizz.backend.entities.ClassEnrollment;
@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long>{
    boolean existsByStudentIdAndClassroomId(Long studentId, Long classroomId);

    List<ClassEnrollment> findAllByStudentId(Long studentId);
    List<ClassEnrollment> findAllByClassroomId(Long classroomId);

    Optional<ClassEnrollment> findByStudentAndClassroom(User student, Classroom classroom);
}
