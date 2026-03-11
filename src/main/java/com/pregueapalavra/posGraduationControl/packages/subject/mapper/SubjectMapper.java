package com.pregueapalavra.posGraduationControl.packages.subject.mapper;

import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;

public class SubjectMapper {

    public static SubjectEntity toCreatedEntity(CreateSubjectRequest requestDTO) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.setName(requestDTO.name());
        return subjectEntity;
    }

    public static SubjectEntity toUpdateEntity(SubjectEntity subjectEntity, UpdateSubjectRequest requestDTO) {
        if(requestDTO.name() != null) {
            subjectEntity.setName(requestDTO.name());
        }
        return subjectEntity;
    }

    public static SubjectResponse toDTO(SubjectEntity subjectEntity) {
        return new SubjectResponse(
                subjectEntity.getId(),
                subjectEntity.getName());
    }
}