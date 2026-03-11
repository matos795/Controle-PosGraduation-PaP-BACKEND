package com.pregueapalavra.posGraduationControl.subjects.factory;

import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;

public class CreateSubjectTestFactory {

    public static CreateSubjectRequest createRequest() {
        return new CreateSubjectRequest(
                "Hermenêutica Avançada");
    }

    public static SubjectEntity createEntity() {
        SubjectEntity entity = new SubjectEntity();
        entity.setId(1L);
        entity.setName("Hermenêutica Avançada");
        return entity;
    }

    public static SubjectResponse createResponse() {
        return new SubjectResponse(
                1L,
                "Hermenêutica Avançada"
        );
    }
}
