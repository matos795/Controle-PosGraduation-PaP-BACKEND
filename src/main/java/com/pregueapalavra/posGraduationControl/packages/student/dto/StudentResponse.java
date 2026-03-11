package com.pregueapalavra.posGraduationControl.packages.student.dto;

public record StudentResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address
) {}