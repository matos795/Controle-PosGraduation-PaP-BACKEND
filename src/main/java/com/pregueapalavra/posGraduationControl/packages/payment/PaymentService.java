package com.pregueapalavra.posGraduationControl.packages.payment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.UpdatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;
import com.pregueapalavra.posGraduationControl.packages.payment.mapper.PaymentMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    public List<PaymentEntity> createPaymentsForEnrollment(EnrollmentEntity enrollmentEntity,
            List<CreatePaymentRequest> paymentRequests) {

        if (paymentRequests == null || paymentRequests.isEmpty()) {
            return List.of();
        }
        return paymentRequests.stream().map(payment -> createPayment(enrollmentEntity, payment)).toList();
    }

    public PaymentEntity createPaymentForEnrollment(EnrollmentEntity enrollmentEntity,
            CreatePaymentRequest paymentRequest) {
        return createPayment(enrollmentEntity, paymentRequest);
    }

    private PaymentEntity createPayment(EnrollmentEntity enrollmentEntity, CreatePaymentRequest paymentRequest) {
        PaymentStatus status = resolveStatus(paymentRequest);
        validateStatusAndDate(status, paymentRequest);
        PaymentEntity paymentEntity = PaymentMapper.toCreatedEntity(paymentRequest, enrollmentEntity);
        return paymentEntity;
    }

    private PaymentStatus resolveStatus(CreatePaymentRequest paymentRequest) {

        PaymentStatus status = paymentRequest.status() == null ? PaymentStatus.PENDING : paymentRequest.status();
        return status;
    }

    private void validateStatusAndDate(PaymentStatus status, CreatePaymentRequest paymentRequest) {

        if (status == PaymentStatus.PAID && paymentRequest.paymentDate() == null) {
            throw new RuntimeException(
                    "Payment date required when status is PAID");
        }
    }

    public void cancelPendingPayments(EnrollmentEntity enrollment) {
        for (PaymentEntity payment : enrollment.getPayments()) {
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.CANCELLED);
            }
        }
    }

    public void updatePayment(PaymentEntity payment, UpdatePaymentRequest request) {
        PaymentStatus status = request.status() == null ? payment.getStatus() : request.status();

        if (status == PaymentStatus.PAID &&
                (payment.getPaymentDate() == null && request.paymentDate() == null)) {
            throw new IllegalArgumentException(
                    "Payment date required when status is PAID");
        }
        PaymentMapper.toUpdatedEntity(request, payment);
    }
}
