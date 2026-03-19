package com.pregueapalavra.posGraduationControl.packages.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private EnrollmentEntity enrollment;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private PaymentStatus status;

    private String description;

    private String coupon;
}
