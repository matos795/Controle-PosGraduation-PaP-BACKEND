package com.pregueapalavra.posGraduationControl.packages.subject.mapper;

import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherSummaryResponse;

public class SubjectMapper {

    public static SubjectEntity toCreatedEntity(CreateSubjectRequest requestDTO) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.setName(requestDTO.name());
        subjectEntity.setDescription(requestDTO.description());
        return subjectEntity;
    }

    public static SubjectEntity toUpdateEntity(SubjectEntity subjectEntity, UpdateSubjectRequest requestDTO) {
        if(requestDTO.name() != null) {
            subjectEntity.setName(requestDTO.name());
        }
        if(requestDTO.description() != null) {
            subjectEntity.setDescription(requestDTO.description());
        }
        return subjectEntity;
    }

    public static SubjectResponse toDTO(SubjectEntity subjectEntity) {
        return new SubjectResponse(
                subjectEntity.getId(),
                subjectEntity.getName(),
                subjectEntity.getDescription(),
                subjectEntity.getClassSessions() != null ? subjectEntity.getClassSessions().size() : 0L
        );
    }

    public static SubjectSummaryResponse toSummaryDTO(SubjectEntity subjectEntity) {
        return new SubjectSummaryResponse(
                subjectEntity.getId(),
                subjectEntity.getName());
    }
}