package com.pregueapalavra.posGraduationControl.packages.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pregueapalavra.posGraduationControl.exception.exceptions.BusinessException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionRepository;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.CreateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.UpdateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.mapper.EnrollmentMapper;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentEntity;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentService;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.UpdatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.StudentRepository;
import com.pregueapalavra.posGraduationControl.packages.student.enums.StudentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final PaymentService paymentService;

    public EnrollmentResponse create(CreateEnrollmentRequest request) {
        if (request.payments() == null || request.payments().isEmpty()) {
            throw new BusinessException("Enrollment must have at least one payment");
        }
        StudentEntity studentEntity = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
        ClassSessionEntity classSessionEntity = classSessionRepository.findById(request.classSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Class Session not found!"));
        EnrollmentEntity enrollment = EnrollmentMapper.toCreatedEntity(request, classSessionEntity, studentEntity);
        enrollment.setPayments(paymentService.createPaymentsForEnrollment(enrollment, request.payments()));
        enrollment = enrollmentRepository.save(enrollment);

        return EnrollmentMapper.toResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentSummaryResponse> getEnrollments(Pageable pageable) {
        Page<EnrollmentEntity> pageEnrollments = enrollmentRepository.findAll(pageable);
        return pageEnrollments.map(EnrollmentMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        EnrollmentEntity entity = findEnrollmentById(id);
        return EnrollmentMapper.toResponse(entity);
    }

    public EnrollmentResponse update(Long id, UpdateEnrollmentRequest request) {
        EnrollmentEntity enrollment = findEnrollmentById(id);
        validateEditable(enrollment);
        validateStatusChange(enrollment, request);
        EnrollmentMapper.updateEntity(request, enrollment);
        return EnrollmentMapper.toResponse(enrollment);
    }

    public EnrollmentResponse cancel(Long id) {
        EnrollmentEntity enrollment = findEnrollmentById(id);
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessException(
                    "Enrollment already cancelled");
        }
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new BusinessException(
                    "Completed enrollment cannot be cancelled");
        }
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        paymentService.cancelPendingPayments(enrollment);
        return EnrollmentMapper.toResponse(enrollment);
    }

    public EnrollmentResponse complete(Long id) {
        EnrollmentEntity enrollment = findEnrollmentById(id);
        validateEditable(enrollment);
        if (enrollment.getPaymentSummaryStatus() == PaymentStatus.PENDING) {
            throw new BusinessException(
                    "Cannot complete enrollment with pending payments");
        }
        enrollment.setStatus(EnrollmentStatus.COMPLETED);

        checkStudentCourseCompletion(enrollment.getStudent().getId());

        return EnrollmentMapper.toResponse(enrollment);
    }

    public EnrollmentResponse addPayment(Long enrollmentId, CreatePaymentRequest request) {
        EnrollmentEntity enrollment = findEnrollmentById(enrollmentId);
        validateEditable(enrollment);
        PaymentEntity payment = paymentService.createPaymentForEnrollment(enrollment, request);
        enrollment.addPayment(payment);
        return EnrollmentMapper.toResponse(enrollment);
    }

    public EnrollmentResponse updatePayment(Long enrollmentId, Long paymentId, UpdatePaymentRequest request) {

        EnrollmentEntity enrollment = findEnrollmentById(enrollmentId);
        validateEditable(enrollment);
        if (enrollment.getPayments() == null) {
            throw new ResourceNotFoundException("No payments for this enrollment");
        }
        PaymentEntity payment = enrollment.getPayments().stream().filter(p -> p.getId().equals(paymentId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Payment not found in this enrollment"));
        paymentService.updatePayment(payment, request);
        return EnrollmentMapper.toResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentSummaryResponse> getByStudent(Long studentId, Pageable pageable) {

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        Page<EnrollmentEntity> page = enrollmentRepository.findByStudentId(studentId, pageable);

        return page.map(EnrollmentMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentSummaryResponse> getByClassSession(Long classSessionId, Pageable pageable) {

        if (!classSessionRepository.existsById(classSessionId)) {
            throw new ResourceNotFoundException("ClassSession not found");
        }

        Page<EnrollmentEntity> page = enrollmentRepository.findByClassSessionId(classSessionId, pageable);

        return page.map(EnrollmentMapper::toSummaryResponse);
    }

    // ----------------------------------------------------------------------------

    private EnrollmentEntity findEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    private void validateEditable(EnrollmentEntity enrollment) {
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED ||
                enrollment.getStatus() == EnrollmentStatus.COMPLETED) {

            throw new BusinessException(
                    "Enrollment cannot be changed in current status");
        }
    }

    private void validateStatusChange(
            EnrollmentEntity enrollment,
            UpdateEnrollmentRequest request) {

        if (request.status() == null) {
            return;
        }

        if (request.status() == EnrollmentStatus.COMPLETED &&
                enrollment.getPaymentSummaryStatus() == PaymentStatus.PENDING) {

            throw new BusinessException(
                    "Cannot complete enrollment with pending payments");
        }
    }

    private void checkStudentCourseCompletion(Long studentId) {

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        var enrollments = enrollmentRepository.findByStudentId(studentId);

        long completedSubjects = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .map(e -> e.getClassSession().getSubject().getId())
                .distinct()
                .count();

        if (completedSubjects >= 8) {
            student.setStatus(StudentStatus.COMPLETED);
        }
    }
}
