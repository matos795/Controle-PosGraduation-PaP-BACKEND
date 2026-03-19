package com.pregueapalavra.posGraduationControl.packages.enrollment.dto;

import java.time.LocalDate;
import java.util.List;

import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.PaymentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentSummaryResponse;

public record EnrollmentResponse(
    Long id,
    StudentSummaryResponse student,
    ClassSessionSummaryResponse classSession,
    EnrollmentType type,
    EnrollmentStatus status,
    LocalDate enrollmentDate,
    List<PaymentSummaryResponse> payments
) {}
