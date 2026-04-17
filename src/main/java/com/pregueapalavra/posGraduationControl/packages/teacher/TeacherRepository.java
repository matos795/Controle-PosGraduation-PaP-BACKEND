package com.pregueapalavra.posGraduationControl.packages.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {

    Page<TeacherEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByEmail(String email);

    long countByIdIn(List<Long> listId);
}
