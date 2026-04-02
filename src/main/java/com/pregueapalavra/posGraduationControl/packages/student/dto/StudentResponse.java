package com.pregueapalavra.posGraduationControl.packages.student.dto;

import com.pregueapalavra.posGraduationControl.packages.student.enums.StudentStatus;

public record StudentResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        StudentStatus status,
        Long enrollmentsCount
) {}