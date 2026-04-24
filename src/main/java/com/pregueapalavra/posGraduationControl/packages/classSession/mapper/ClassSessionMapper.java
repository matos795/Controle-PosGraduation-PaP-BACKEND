package com.pregueapalavra.posGraduationControl.packages.classSession.mapper;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionResponse;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.CreateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.UpdateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.mapper.SubjectMapper;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.mapper.TeacherMapper;

public class ClassSessionMapper {

    public static ClassSessionEntity toCreatedEntity(CreateClassSessionRequest requestDTO, TeacherEntity teacher, SubjectEntity subject) {
        ClassSessionEntity classSessionEntity = new ClassSessionEntity();
        classSessionEntity.setTitle(requestDTO.title());
        classSessionEntity.setInitialDate(requestDTO.initialDate());
        classSessionEntity.setFinalDate(requestDTO.finalDate());
        classSessionEntity.setTeacher(teacher);
        classSessionEntity.setSubject(subject);
        return classSessionEntity;
    }

    public static ClassSessionEntity toUpdateEntity(ClassSessionEntity classSessionEntity, UpdateClassSessionRequest requestDTO, TeacherEntity teacher, SubjectEntity subject) {
        if(requestDTO.title() != null) {
            classSessionEntity.setTitle(requestDTO.title());
        }
        if(requestDTO.initialDate() != null) {
            classSessionEntity.setInitialDate(requestDTO.initialDate());
        }
        if(requestDTO.finalDate() != null) {
            classSessionEntity.setFinalDate(requestDTO.finalDate());
        }
        if(requestDTO.teacherId() != null) {
            classSessionEntity.setTeacher(teacher);
        }
        if(requestDTO.subjectId() != null) {
            classSessionEntity.setSubject(subject);
        }
        return classSessionEntity;
    }

    public static ClassSessionResponse toDTO(ClassSessionEntity classSessionEntity) {
        return new ClassSessionResponse(
                classSessionEntity.getId(),
                classSessionEntity.getTitle(),
                SubjectMapper.toSummaryDTO(classSessionEntity.getSubject()),
                classSessionEntity.getInitialDate(),
                classSessionEntity.getFinalDate(),
                TeacherMapper.toSummaryDTO(classSessionEntity.getTeacher()),
                classSessionEntity.getEnrollments() != null ? classSessionEntity.getEnrollments().size() : 0L
                );
    }

    public static ClassSessionSummaryResponse toSummaryResponse(ClassSessionEntity classSessionEntity) {
        return new ClassSessionSummaryResponse(
                classSessionEntity.getId(),
                classSessionEntity.getTitle());
    }
}