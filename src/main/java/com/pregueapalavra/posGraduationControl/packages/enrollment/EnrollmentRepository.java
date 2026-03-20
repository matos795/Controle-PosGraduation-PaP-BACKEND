package com.pregueapalavra.posGraduationControl.packages.enrollment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    List<EnrollmentEntity> findByStudentId(Long studentId);

    Page<EnrollmentEntity> findByStudentId(Long studentId, Pageable pageable);

    Page<EnrollmentEntity> findByClassSessionId(Long classSessionId, Pageable pageable);
}
