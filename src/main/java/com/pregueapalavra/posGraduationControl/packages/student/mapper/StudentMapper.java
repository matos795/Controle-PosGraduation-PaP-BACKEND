package com.pregueapalavra.posGraduationControl.packages.student.mapper;

import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;

public class StudentMapper {

    public static StudentEntity toCreatedEntity(CreateStudentRequest requestDTO) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName(requestDTO.name());
        studentEntity.setEmail(requestDTO.email());
        studentEntity.setPhone(requestDTO.phone());
        studentEntity.setAddress(requestDTO.address());
        return studentEntity;
    }

    public static StudentEntity toUpdateEntity(StudentEntity studentEntity, UpdateStudentRequest requestDTO) {
        if(requestDTO.name() != null) {
            studentEntity.setName(requestDTO.name());
        }
        if(requestDTO.email() != null) {
            studentEntity.setEmail(requestDTO.email());
        }
        if(requestDTO.phone() != null) {
            studentEntity.setPhone(requestDTO.phone());
        }
        if(requestDTO.address() != null) {
            studentEntity.setAddress(requestDTO.address());
        }
        return studentEntity;
    }

    public static StudentResponse toDTO(StudentEntity studentEntity) {
        return new StudentResponse(
                studentEntity.getId(),
                studentEntity.getName(),
                studentEntity.getEmail(),
                studentEntity.getPhone(),
                studentEntity.getAddress());
    }
}
