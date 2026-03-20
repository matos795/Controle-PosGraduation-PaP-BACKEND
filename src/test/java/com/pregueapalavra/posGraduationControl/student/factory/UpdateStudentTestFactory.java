package com.pregueapalavra.posGraduationControl.student.factory;

import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.enums.StudentStatus;

public class UpdateStudentTestFactory {

    public static UpdateStudentRequest updateRequest() {
        return new UpdateStudentRequest(
                "Alexandre Oliveira",
                "novo@email.com",
                "11888888888",
                "Rua B");
    }

    public static StudentEntity updateEntity() {
        StudentEntity entity = new StudentEntity();
        entity.setId(1L);
        entity.setName("Alexandre Oliveira");
        entity.setEmail("alexandre@email.com");
        entity.setPhone("11888888888");
        entity.setAddress("Rua B");
        return entity;
    }

    public static StudentResponse updateResponse() {
        return new StudentResponse(
                1L,
                "Alexandre Oliveira",
                "alexandre@email.com",
                "11888888888",
                "Rua B",
                StudentStatus.IN_PROGRESS
        );
    }
}
