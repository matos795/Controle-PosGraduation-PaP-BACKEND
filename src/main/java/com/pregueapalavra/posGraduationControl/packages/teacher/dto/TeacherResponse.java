package com.pregueapalavra.posGraduationControl.packages.teacher.dto;

public record TeacherResponse(
    Long id,
    String name,
    String email,
    String phone,
    String address,
    Long classSessionCount
) {

}
