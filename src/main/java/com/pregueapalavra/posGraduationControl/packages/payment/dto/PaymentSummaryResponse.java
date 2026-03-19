package com.pregueapalavra.posGraduationControl.packages.payment.dto;

import java.math.BigDecimal;

import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

public record PaymentSummaryResponse(
    Long id,
    BigDecimal amount,
    PaymentStatus status
) {}
