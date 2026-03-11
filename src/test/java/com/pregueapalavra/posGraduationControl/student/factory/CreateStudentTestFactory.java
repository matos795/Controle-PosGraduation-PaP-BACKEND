package com.pregueapalavra.posGraduationControl.student.factory;

import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;

public class CreateStudentTestFactory {

    public static CreateStudentRequest createRequest() {
        return new CreateStudentRequest(
                "João Silva",
                "joao@email.com",
                "11999999999",
                "Rua A");
    }

    public static StudentEntity createEntity() {
        StudentEntity entity = new StudentEntity();
        entity.setId(1L);
        entity.setName("João Silva");
        entity.setEmail("joao@email.com");
        entity.setPhone("11999999999");
        entity.setAddress("Rua A");
        return entity;
    }

    public static StudentResponse createResponse() {
        return new StudentResponse(
                1L,
                "João Silva",
                "joao@email.com",
                "11999999999",
                "Rua A"
        );
    }
}
