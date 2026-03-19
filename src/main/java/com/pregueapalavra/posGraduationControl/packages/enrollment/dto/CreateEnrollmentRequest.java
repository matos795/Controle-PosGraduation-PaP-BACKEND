package com.pregueapalavra.posGraduationControl.packages.enrollment.dto;

import java.time.LocalDate;
import java.util.List;

import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(

    @NotNull
    Long studentId,

    @NotNull
    Long classSessionId,

    @NotNull
    EnrollmentType type,

    @NotNull
    LocalDate enrollmentDate,

    @NotNull
    List<CreatePaymentRequest> payments
) {}
