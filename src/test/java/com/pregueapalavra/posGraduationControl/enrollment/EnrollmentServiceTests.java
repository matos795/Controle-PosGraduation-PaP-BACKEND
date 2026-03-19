package com.pregueapalavra.posGraduationControl.enrollment;

import com.pregueapalavra.posGraduationControl.classSession.factory.CreateClassSessionTestFactory;
import com.pregueapalavra.posGraduationControl.enrollment.factory.EnrollmentFactory;
import com.pregueapalavra.posGraduationControl.exception.exceptions.BusinessException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionRepository;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentEntity;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentRepository;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentService;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.CreateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.dto.UpdateEnrollmentRequest;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentType;
import com.pregueapalavra.posGraduationControl.packages.payment.PaymentService;
import com.pregueapalavra.posGraduationControl.packages.payment.enums.PaymentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.StudentRepository;
import com.pregueapalavra.posGraduationControl.student.factory.CreateStudentTestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ClassSessionRepository classSessionRepository;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private EnrollmentService enrollmentService;

    @Nested
    class Create {

        @Test
        void shouldCreateEnrollment() {

            // Arrange
            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            var request = EnrollmentFactory.enrollmentRequest();

            when(studentRepository.findById(request.studentId())).thenReturn(Optional.of(student));
            when(classSessionRepository.findById(request.classSessionId())).thenReturn(Optional.of(classSession));
            when(paymentService.createPaymentsForEnrollment(any(), any())).thenReturn(enrollment.getPayments());
            when(enrollmentRepository.save(any())).thenReturn(enrollment);

            // Act
            var response = enrollmentService.create(request);

            // Assert
            assertNotNull(response);
            assertEquals(enrollment.getClassSession().getId(), response.classSession().id());
            assertEquals(enrollment.getStudent().getId(), response.student().id());
            assertEquals(enrollment.getEnrollmentDate(), response.enrollmentDate());
            assertEquals(enrollment.getId(), response.id());
            assertEquals(1L, response.payments().size());

            verify(studentRepository).findById(request.studentId());
            verify(classSessionRepository).findById(request.classSessionId());
            verify(paymentService).createPaymentsForEnrollment(any(), any());
            verify(enrollmentRepository).save(any());
            verifyNoMoreInteractions(enrollmentRepository);
        }

        @Test
        void shouldThrowExceptionWhenStudentNotFound() {

            // Arrange
            var request = EnrollmentFactory.enrollmentRequest();

            when(studentRepository.findById(request.studentId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                enrollmentService.create(request);
            });

            verify(studentRepository).findById(request.studentId());
            verifyNoInteractions(classSessionRepository);
            verifyNoInteractions(enrollmentRepository);
        }

        @Test
        void shouldThrowExceptionWhenClassSessionIdNotFound() {

            // Arrange
            var student = CreateStudentTestFactory.createEntity();
            var request = EnrollmentFactory.enrollmentRequest();

            when(studentRepository.findById(request.studentId())).thenReturn(Optional.of(student));
            when(classSessionRepository.findById(request.classSessionId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                enrollmentService.create(request);
            });

            verify(studentRepository).findById(request.studentId());
            verify(classSessionRepository).findById(request.classSessionId());
            verifyNoInteractions(enrollmentRepository);
        }

        @Test
        void shouldThrowWhenPaymentsIsNull() {

            var request = new CreateEnrollmentRequest(
                    1L,
                    1L,
                    EnrollmentType.ONLINE,
                    LocalDate.now(),
                    null);

            assertThrows(BusinessException.class, () -> {
                enrollmentService.create(request);
            });

            verifyNoInteractions(studentRepository);
            verifyNoInteractions(classSessionRepository);
            verifyNoInteractions(enrollmentRepository);
        }

        @Test
        void shouldThrowWhenPaymentsIsEmpty() {

            var request = new CreateEnrollmentRequest(
                    1L,
                    1L,
                    EnrollmentType.ONLINE,
                    LocalDate.now(),
                    List.of());

            assertThrows(BusinessException.class, () -> {
                enrollmentService.create(request);
            });

            verifyNoInteractions(studentRepository);
            verifyNoInteractions(classSessionRepository);
            verifyNoInteractions(enrollmentRepository);
        }
    }

    @Nested
    class getAll {

        @Test
        void shouldReturnPage() {

            // Arrange
            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            var pageable = PageRequest.of(0, 10);
            Page<EnrollmentEntity> page = new PageImpl<>(List.of(enrollment));

            when(enrollmentRepository.findAll(pageable)).thenReturn(page);

            var result = enrollmentService.getEnrollments(pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());

            verify(enrollmentRepository).findAll(pageable);
        }

        @Test
        void shouldReturnEmptyPage() {

            // Arrange
            var pageable = PageRequest.of(0, 10);
            Page<EnrollmentEntity> page = new PageImpl<>(List.of());

            when(enrollmentRepository.findAll(pageable)).thenReturn(page);

            var result = enrollmentService.getEnrollments(pageable);

            assertNotNull(result);
            assertEquals(0, result.getContent().size());

            verify(enrollmentRepository).findAll(pageable);
        }
    }

    @Nested
    class getById {

        @Test
        void shouldReturnEnrollment() {

            // Arrange
            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            // Act
            var response = enrollmentService.getEnrollmentById(1L);

            // Assert
            assertNotNull(response);
            assertEquals(enrollment.getId(), response.id());
            assertEquals(student.getId(), response.student().id());
            assertEquals(classSession.getId(), response.classSession().id());

            verify(enrollmentRepository).findById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                enrollmentService.getEnrollmentById(1L);
            });

            verify(enrollmentRepository).findById(1L);
        }
    }

    @Nested
    class update {

        @Test
        void shouldUpdateEnrollment() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            var request = EnrollmentFactory.updateRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.update(1L, request);

            assertNotNull(response);
            assertEquals(request.type(), response.type());

            verify(enrollmentRepository).findById(1L);
        }

        @Test
        void shouldThrowWhenEnrollmentNotFound() {

            var request = EnrollmentFactory.updateRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.update(1L, request));

            verify(enrollmentRepository).findById(1L);
        }

        @Test
        void shouldNotUpdateWhenCancelled() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);
            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            var request = EnrollmentFactory.updateRequest();

            when(enrollmentRepository.findById(1L))
                    .thenReturn(Optional.of(enrollment));

            assertThrows(
                    BusinessException.class,
                    () -> enrollmentService.update(1L, request));
        }

        @Test
        void shouldNotUpdateWhenCompleted() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);
            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            var request = EnrollmentFactory.updateRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.update(1L, request));
        }

        @Test
        void shouldNotCompleteWhenPaymentPending() {

            var enrollment = spy(
                    EnrollmentFactory.enrollmentCreated(
                            CreateStudentTestFactory.createEntity(),
                            CreateClassSessionTestFactory.createEntity()));

            var request = new UpdateEnrollmentRequest(
                    null,
                    null,
                    EnrollmentStatus.COMPLETED);

            when(enrollment.getPaymentSummaryStatus()).thenReturn(PaymentStatus.PENDING);
            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.update(1L, request));
        }

        @Test
        void shouldAllowCompleteWhenPaymentPaid() {

            var enrollment = spy(
                    EnrollmentFactory.enrollmentCreated(
                            CreateStudentTestFactory.createEntity(),
                            CreateClassSessionTestFactory.createEntity()));

            var request = new UpdateEnrollmentRequest(
                    null,
                    null,
                    EnrollmentStatus.COMPLETED);

            when(enrollment.getPaymentSummaryStatus()).thenReturn(PaymentStatus.PAID);
            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.update(1L, request);

            assertEquals(EnrollmentStatus.COMPLETED, response.status());
        }
    }

    @Nested
    class cancel {

        @Test
        void shouldCancelEnrollment() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.cancel(1L);

            assertEquals(EnrollmentStatus.CANCELLED, response.status());

            verify(paymentService).cancelPendingPayments(enrollment);
        }

        @Test
        void shouldThrowWhenEnrollmentNotFound() {

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.cancel(1L));

            verify(enrollmentRepository).findById(1L);
        }

        @Test
        void shouldThrowWhenAlreadyCancelled() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.cancel(1L));
        }

        @Test
        void shouldThrowWhenCompleted() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.cancel(1L));
        }
    }

    @Nested
    class complete {

        @Test
        void shouldCompleteEnrollment() {

            var enrollment = spy(
                    EnrollmentFactory.enrollmentCreated(
                            CreateStudentTestFactory.createEntity(),
                            CreateClassSessionTestFactory.createEntity()));

            when(enrollment.getPaymentSummaryStatus()).thenReturn(PaymentStatus.PAID);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.complete(1L);

            assertEquals(EnrollmentStatus.COMPLETED, response.status());
        }

        @Test
        void shouldThrowWhenEnrollmentNotFound() {

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.complete(1L));
        }

        @Test
        void shouldThrowWhenCancelled() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.complete(1L));
        }

        @Test
        void shouldThrowWhenAlreadyCompleted() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.complete(1L));
        }

        @Test
        void shouldThrowWhenPaymentPending() {

            var enrollment = spy(
                    EnrollmentFactory.enrollmentCreated(
                            CreateStudentTestFactory.createEntity(),
                            CreateClassSessionTestFactory.createEntity()));

            when(enrollment.getPaymentSummaryStatus()).thenReturn(PaymentStatus.PENDING);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.complete(1L));
        }

        @Test
        void shouldCompleteWhenPaymentPaid() {

            var enrollment = spy(
                    EnrollmentFactory.enrollmentCreated(
                            CreateStudentTestFactory.createEntity(),
                            CreateClassSessionTestFactory.createEntity()));

            when(enrollment.getPaymentSummaryStatus()).thenReturn(PaymentStatus.PAID);

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.complete(1L);

            assertEquals(EnrollmentStatus.COMPLETED, response.status());
        }
    }

    @Nested
    class addPayment {

        @Test
        void shouldAddPayment() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);
            var payment = EnrollmentFactory.paymentCreated();
            var request = EnrollmentFactory.paymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
            when(paymentService.createPaymentForEnrollment(enrollment, request)).thenReturn(payment);

            var response = enrollmentService.addPayment(1L, request);
            assertNotNull(response);

            verify(paymentService).createPaymentForEnrollment(enrollment, request);
        }

        @Test
        void shouldThrowWhenEnrollmentNotFound() {

            var request = EnrollmentFactory.paymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.addPayment(1L, request));
        }

        @Test
        void shouldThrowWhenCancelled() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            var request = EnrollmentFactory.paymentRequest();

            when(enrollmentRepository.findById(1L))
                    .thenReturn(Optional.of(enrollment));

            assertThrows(
                    BusinessException.class,
                    () -> enrollmentService.addPayment(1L, request));
        }

        @Test
        void shouldThrowWhenCompleted() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            var request = EnrollmentFactory.paymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.addPayment(1L, request));
        }
    }

    @Nested
    class UpdatePayment {

        @Test
        void shouldUpdatePayment() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();

            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            var payment = enrollment.getPayments().get(0);

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            var response = enrollmentService.updatePayment(
                    1L,
                    payment.getId(),
                    request);

            assertNotNull(response);

            verify(paymentService).updatePayment(payment, request);
        }

        @Test
        void shouldThrowWhenEnrollmentNotFound() {

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> enrollmentService.updatePayment(
                            1L,
                            1L,
                            request));
        }

        @Test
        void shouldThrowWhenCancelled() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.CANCELLED);

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.updatePayment(
                    1L,
                    1L,
                    request));
        }

        @Test
        void shouldThrowWhenCompleted() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setStatus(EnrollmentStatus.COMPLETED);

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(BusinessException.class, () -> enrollmentService.updatePayment(
                    1L,
                    1L,
                    request));
        }

        @Test
        void shouldThrowWhenPaymentsNull() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            enrollment.setPayments(null);

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.updatePayment(
                    1L,
                    1L,
                    request));
        }

        @Test
        void shouldThrowWhenPaymentNotFound() {

            var student = CreateStudentTestFactory.createEntity();
            var classSession = CreateClassSessionTestFactory.createEntity();
            var enrollment = EnrollmentFactory.enrollmentCreated(student, classSession);

            var request = EnrollmentFactory.updatePaymentRequest();

            when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

            assertThrows(ResourceNotFoundException.class, () -> enrollmentService.updatePayment(
                    1L,
                    999L,
                    request));
        }
    }
}
