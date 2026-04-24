package com.pregueapalavra.posGraduationControl.packages.classSession.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record UpdateClassSessionRequest(

    @Size(max = 100)
    String title,
    LocalDate initialDate,
    LocalDate finalDate,
    Long teacherId,
    Long subjectId
) {}
