package com.pregueapalavra.posGraduationControl.packages.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.CreateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.EnrollmentSummaryResponse;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.UpdateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.CreatePaymentRequest;
import com.pregueapalavra.posGraduationControl.packages.payment.dto.UpdatePaymentRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.create(request);
    }

    @GetMapping
    public Page<EnrollmentSummaryResponse> getEnrollments(Pageable pageable) {
        return enrollmentService.getEnrollments(pageable);
    }

    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollmentById(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id);
    }

    @PatchMapping("/{id}")
    public EnrollmentResponse update(@PathVariable Long id, @RequestBody @Valid UpdateEnrollmentRequest request) {
        return enrollmentService.update(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public EnrollmentResponse cancel(@PathVariable Long id) {
        return enrollmentService.cancel(id);
    }

    @PatchMapping("/{id}/complete")
    public EnrollmentResponse complete(@PathVariable Long id) {
        return enrollmentService.complete(id);
    }

    @PostMapping("/{id}/payments")
    public EnrollmentResponse addPayment(@PathVariable Long id, @RequestBody @Valid CreatePaymentRequest request) {
        return enrollmentService.addPayment(id, request);
    }

    @PatchMapping("/{id}/payments/{paymentId}")
    public EnrollmentResponse updatePayment(@PathVariable Long id, @PathVariable Long paymentId, @RequestBody @Valid UpdatePaymentRequest request) {
        return enrollmentService.updatePayment(id, paymentId, request);
    }
}
