package com.pregueapalavra.posGraduationControl.classSession.factory;

import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionResponse;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.UpdateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherSummaryResponse;

    public class UpdateClassSessionTestFactory {

    public static UpdateClassSessionRequest updateRequest() {
        return new UpdateClassSessionRequest(
                "Hermenêutica Avançada - grupo 2025",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 5),
                1L
        );
    }

    public static TeacherEntity teacher() {
        TeacherEntity teacher = new TeacherEntity();
        teacher.setId(1L);
        teacher.setName("Pr. João");
        return teacher;
    }

    public static SubjectEntity subject() {
        SubjectEntity subject = new SubjectEntity();
        subject.setId(1L);
        subject.setName("Hermenêutica");
        return subject;
    }

    public static ClassSessionEntity updateEntity() {

        ClassSessionEntity entity = new ClassSessionEntity();
        entity.setId(1L);
        entity.setTitle("Hermenêutica Avançada - grupo 2025");
        entity.setSubject(subject());
        entity.setInitialDate(LocalDate.of(2025, 7, 1));
        entity.setFinalDate(LocalDate.of(2025, 7, 5));
        entity.setTeacher(teacher());

        return entity;
    }

    public static ClassSessionResponse updateResponse() {

        SubjectResponse subject = new SubjectResponse(
                1L,
                "Hermenêutica",
                null, 0L
        );

        TeacherSummaryResponse teacher = new TeacherSummaryResponse(
                1L,
                "Pr. João"
        );

        return new ClassSessionResponse(
                1L,
                "Hermenêutica Avançada - grupo 2025",
                subject,
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 5),
                teacher
        );
    }
}