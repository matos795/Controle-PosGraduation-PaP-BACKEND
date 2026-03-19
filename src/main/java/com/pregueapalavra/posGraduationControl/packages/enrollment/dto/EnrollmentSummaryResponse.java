package com.pregueapalavra.posGraduationControl.packages.enrollment.dto;

import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

public record EnrollmentSummaryResponse(
    Long id,
    String classSessionTitle,
    String studentName,
    EnrollmentType type,
    EnrollmentStatus status,
    LocalDate enrollmenDate,
    PaymentStatus paymentStatus
) {}
