package com.pregueapalavra.posGraduationControl.packages.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

public record UpdatePaymentRequest(
        BigDecimal amount,
        LocalDate paymentDate,
        PaymentStatus status,
        String description,
        String coupon
) {}