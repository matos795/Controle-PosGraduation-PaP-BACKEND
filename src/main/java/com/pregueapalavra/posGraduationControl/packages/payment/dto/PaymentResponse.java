package com.pregueapalavra.posGraduationControl.packages.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

public record PaymentResponse(
    Long id,
    Long enrollmentId,
    BigDecimal amount,
    LocalDate dueDate,
    LocalDate paymentDate,
    PaymentStatus status,
    String description,
    String coupon
) {}
