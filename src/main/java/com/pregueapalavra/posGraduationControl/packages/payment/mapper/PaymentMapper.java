package com.pregueapalavra.posGraduationControl.packages.payment.mapper;

import java.util.List;

import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.PaymentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.UpdatePaymentRequest;

public class PaymentMapper {

    public static PaymentEntity toCreatedEntity(CreatePaymentRequest requestDTO, EnrollmentEntity enrollmentEntity) {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setEnrollment(enrollmentEntity);
        paymentEntity.setAmount(requestDTO.amount());
        paymentEntity.setPaymentDate(requestDTO.paymentDate());
        paymentEntity.setCoupon(requestDTO.coupon());
        paymentEntity.setDescription(requestDTO.description());
        paymentEntity.setStatus(requestDTO.status());
        return paymentEntity;
    }

    public static List<PaymentSummaryResponse> toSummaryListResponse(List<PaymentEntity> payments) {

        return payments.stream().map(PaymentMapper::toSummaryResponse).toList();
    }

    public static PaymentSummaryResponse toSummaryResponse(PaymentEntity paymentEntity) {

        return new PaymentSummaryResponse(
                paymentEntity.getId(),
                paymentEntity.getAmount(),
                paymentEntity.getStatus());
    }

    public static void toUpdatedEntity(UpdatePaymentRequest request, PaymentEntity payment) {

        if (request.amount() != null) {
            payment.setAmount(request.amount());
        }
        if (request.paymentDate() != null) {
            payment.setPaymentDate(request.paymentDate());
        }
        if (request.status() != null) {
            payment.setStatus(request.status());
        }
        if (request.description() != null) {
            payment.setDescription(request.description());
        }
        if (request.coupon() != null) {
            payment.setCoupon(request.coupon());
        }
        if (request.amount() != null) {
            payment.setAmount(request.amount());
        }
    }
}
