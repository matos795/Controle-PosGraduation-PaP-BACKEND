package com.pregueapalavra.posGraduationControl.subjects.factory;

import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;

public class UpdateSubjectTestFactory {

    public static UpdateSubjectRequest updateRequest() {
        return new UpdateSubjectRequest(
                "Hermenêutica Atualizada");
    }

    public static SubjectEntity updateEntity() {
        SubjectEntity entity = new SubjectEntity();
        entity.setId(1L);
        entity.setName("Hermenêutica Avançada");

        return entity;
    }

    public static SubjectResponse updateResponse() {
        return new SubjectResponse(
                1L,
                "Hermenêutica Atualizada"
        );
    }
}
