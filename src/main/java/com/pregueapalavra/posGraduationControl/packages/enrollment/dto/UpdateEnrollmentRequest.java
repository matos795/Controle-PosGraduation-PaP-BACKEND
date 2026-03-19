package com.pregueapalavra.posGraduationControl.packages.enrollment.dto;

import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;

public record UpdateEnrollmentRequest(
        EnrollmentType type,
        LocalDate enrollmentDate,
        EnrollmentStatus status
    ) {}
