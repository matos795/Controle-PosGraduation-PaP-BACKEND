package com.pregueapalavra.posGraduationControl.packages.teacher.mapper;

import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.CreateTeacherRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherResponse;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.UpdateTeacherRequest;

public class TeacherMapper {

    public static TeacherEntity toCreatedEntity(CreateTeacherRequest requestDTO) {
        TeacherEntity teacherEntity = new TeacherEntity();
        
        teacherEntity.setName(requestDTO.name());

        if(requestDTO.email() != null) {
            teacherEntity.setEmail(requestDTO.email());
        }
        if(requestDTO.phone() != null) {
            teacherEntity.setPhone(requestDTO.phone());
        }
        if(requestDTO.address() != null) {
            teacherEntity.setAddress(requestDTO.address());
        }
        return teacherEntity;
    }

    public static TeacherEntity toUpdateEntity(TeacherEntity teacherEntity, UpdateTeacherRequest requestDTO) {
        if(requestDTO.name() != null) {
            teacherEntity.setName(requestDTO.name());
        }
        if(requestDTO.email() != null) {
            teacherEntity.setEmail(requestDTO.email());
        }
        if(requestDTO.phone() != null) {
            teacherEntity.setPhone(requestDTO.phone());
        }
        if(requestDTO.address() != null) {
            teacherEntity.setAddress(requestDTO.address());
        }
        return teacherEntity;
    }

    public static TeacherResponse toDTO(TeacherEntity teacherEntity) {
        return new TeacherResponse(
                teacherEntity.getId(),
                teacherEntity.getName(),
                teacherEntity.getEmail(),
                teacherEntity.getPhone(),
                teacherEntity.getAddress(),
                teacherEntity.getClassSessions() != null ? teacherEntity.getClassSessions().size() : 0L);
    }

    public static TeacherSummaryResponse toSummaryDTO(TeacherEntity teacherEntity) {
        return new TeacherSummaryResponse(
            teacherEntity.getId(),
            teacherEntity.getName());
    }
}
