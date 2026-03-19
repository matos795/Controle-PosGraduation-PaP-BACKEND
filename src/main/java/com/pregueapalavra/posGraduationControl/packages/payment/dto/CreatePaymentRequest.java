package com.pregueapalavra.posGraduationControl.packages.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest(
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    BigDecimal amount,

    LocalDate paymentDate,
    
    PaymentStatus status,

    @Size(max = 100, message = "Description max 100 chars")
    String description,
    @Size(max = 25, message = "Coupon max 25 chars")
    String coupon
) {}
