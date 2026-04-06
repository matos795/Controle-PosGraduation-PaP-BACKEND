package com.pregueapalavra.posGraduationControl.packages.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pregueapalavra.posGraduationControl.packages.student.enums.StudentStatus;

public interface StudentRepository extends JpaRepository<StudentEntity, Long>{

    Page<StudentEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<StudentEntity> findByStatus(StudentStatus status, Pageable pageable);

    Page<StudentEntity> findByNameContainingIgnoreCaseAndStatus(String name, StudentStatus status, Pageable pageable);

    boolean existsByEmail(String email);
}
