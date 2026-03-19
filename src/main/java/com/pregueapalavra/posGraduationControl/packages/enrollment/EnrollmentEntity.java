package com.pregueapalavra.posGraduationControl.packages.enrollment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "class_session_id", nullable = false)
    private ClassSessionEntity classSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments;

    public PaymentStatus getPaymentSummaryStatus() {

        if (payments == null || payments.isEmpty()) {
            return PaymentStatus.PENDING;
        }

        boolean hasPending = false;
        boolean allPaid = true;

        for (PaymentEntity p : payments) {
            if (p.getStatus() == PaymentStatus.PENDING) {
                hasPending = true;
                allPaid = false;
            }
            if (p.getStatus() != PaymentStatus.PAID) {
                allPaid = false;
            }
        }

        if (hasPending) {
            return PaymentStatus.PENDING;
        }
        if (allPaid) {
            return PaymentStatus.PAID;
        }
        return PaymentStatus.PENDING;
    }

    public void addPayment(PaymentEntity payment) {
        if (payments == null) {
            payments = new ArrayList<>();
        }
        payments.add(payment);
    }
}
