package com.pregueapalavra.posGraduationControl.packages.subject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

    boolean existsByName(String name);
}
