package com.pregueapalavra.posGraduationControl.packages.enrollment.mapper;

import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.mapper.ClassSessionMapper;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.CreateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.UpdateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.payment.mapper.PaymentMapper;
import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.mapper.StudentMapper;

public class EnrollmentMapper {

    public static EnrollmentEntity toCreatedEntity(CreateEnrollmentRequest requestDTO,
            ClassSessionEntity classSessionEntity, StudentEntity studentEntity) {
        EnrollmentEntity enrollmentEntity = new EnrollmentEntity();
        enrollmentEntity.setStudent(studentEntity);
        enrollmentEntity.setClassSession(classSessionEntity);
        enrollmentEntity.setStatus(EnrollmentStatus.ENROLLED);
        enrollmentEntity.setType(requestDTO.type());
        enrollmentEntity.setEnrollmentDate(requestDTO.enrollmentDate());
        return enrollmentEntity;
    }

    public static EnrollmentResponse toResponse(EnrollmentEntity enrollmentEntity) {
        return new EnrollmentResponse(
                enrollmentEntity.getId(),
                StudentMapper.toSummaryResponse(enrollmentEntity.getStudent()),
                ClassSessionMapper.toSummaryResponse(enrollmentEntity.getClassSession()),
                enrollmentEntity.getType(),
                enrollmentEntity.getStatus(),
                enrollmentEntity.getEnrollmentDate(),
                PaymentMapper.toSummaryListResponse(enrollmentEntity.getPayments()));
    }

    public static EnrollmentSummaryResponse toSummaryResponse(EnrollmentEntity enrollmentEntity) {
        return new EnrollmentSummaryResponse(
                enrollmentEntity.getId(),
                enrollmentEntity.getClassSession().getTitle(),
                enrollmentEntity.getStudent().getName(),
                enrollmentEntity.getType(),
                enrollmentEntity.getStatus(),
                enrollmentEntity.getEnrollmentDate(),
                enrollmentEntity.getPaymentSummaryStatus());
    }

    public static void updateEntity(UpdateEnrollmentRequest request,
            EnrollmentEntity entity) {
        if (request.type() != null) {
            entity.setType(request.type());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        if (request.enrollmentDate() != null) {
            entity.setEnrollmentDate(request.enrollmentDate());
        }
    }
}
