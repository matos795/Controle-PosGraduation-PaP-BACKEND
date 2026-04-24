package com.pregueapalavra.posGraduationControl.packages.classSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSessionEntity, Long>,
    JpaSpecificationExecutor<ClassSessionEntity> {

    Page<ClassSessionEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    long countByIdIn(List<Long> listId);
}

