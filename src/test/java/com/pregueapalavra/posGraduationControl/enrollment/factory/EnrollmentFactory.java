package com.pregueapalavra.posGraduationControl.enrollment.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.CreateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.UpdateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.PaymentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.UpdatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentSummaryResponse;

public class EnrollmentFactory {

    public static EnrollmentEntity enrollmentCreated(StudentEntity studentEntity,
            ClassSessionEntity classSessionEntity) {
        EnrollmentEntity e = new EnrollmentEntity();
        e.setId(1L);
        e.setStudent(studentEntity);
        e.setClassSession(classSessionEntity);
        e.setType(EnrollmentType.ONLINE);
        e.setStatus(EnrollmentStatus.ENROLLED);
        e.setEnrollmentDate(LocalDate.now());

        PaymentEntity payment = paymentCreated();

        var list = new ArrayList<PaymentEntity>();
        list.add(payment);

        e.setPayments(list);
        return e;
    }

    public static CreateEnrollmentRequest enrollmentRequest() {

        CreateEnrollmentRequest e = new CreateEnrollmentRequest(
                1L,
                1L,
                EnrollmentType.ONLINE,
                LocalDate.now(),
                List.of(paymentRequest()));
        return e;
    }

    public static PaymentEntity paymentCreated() {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setAmount(BigDecimal.valueOf(400.00));
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    public static CreatePaymentRequest paymentRequest() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                BigDecimal.valueOf(400.00),
                LocalDate.now(),
                PaymentStatus.PENDING,
                null,
                null);
        return request;
    }

    public static UpdateEnrollmentRequest updateRequest() {
        return new UpdateEnrollmentRequest(
                EnrollmentType.PRESENTIAL,
                LocalDate.now(),
                null);
    }

    public static UpdatePaymentRequest updatePaymentRequest() {
        return new UpdatePaymentRequest(
                null,
                null,
                null,
                null,
                null);
    }

    public static EnrollmentResponse enrollmentResponse() {
        return new EnrollmentResponse(
                1L,
                studentSummary(),
                classSessionSummary(),
                EnrollmentType.ONLINE,
                EnrollmentStatus.ENROLLED,
                LocalDate.now(),
                List.of(paymentSummary()));
    }

    public static StudentSummaryResponse studentSummary() {
        return new StudentSummaryResponse(
                1L,
                "John Doe");
    }

    public static ClassSessionSummaryResponse classSessionSummary() {
        return new ClassSessionSummaryResponse(
                1L,
                "Java");
    }

    public static PaymentSummaryResponse paymentSummary() {
        return new PaymentSummaryResponse(
                1L,
                BigDecimal.valueOf(100),
                PaymentStatus.PENDING);
    }

    public static EnrollmentSummaryResponse enrollmentSummaryResponse() {
        return new EnrollmentSummaryResponse(
                1L,
                "Java",
                "John",
                EnrollmentType.ONLINE,
                EnrollmentStatus.ENROLLED,
                LocalDate.now(),
                PaymentStatus.PENDING);
    }
}
