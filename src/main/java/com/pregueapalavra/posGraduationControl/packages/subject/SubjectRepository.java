package com.pregueapalavra.posGraduationControl.packages.subject;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

    List<SubjectEntity> findAllByNameContainingIgnoreCase(String name, Sort sort);

    boolean existsByName(String name);

    long countByIdIn(Collection<Long> ids);

}
