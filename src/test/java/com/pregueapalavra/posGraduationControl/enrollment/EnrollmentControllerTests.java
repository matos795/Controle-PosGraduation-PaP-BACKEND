package com.pregueapalavra.posGraduationControl.enrollment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pregueapalavra.posGraduationControl.enrollment.factory.EnrollmentFactory;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentController;
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentService;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Create {

        @Test
        void shouldCreateEnrollment() throws Exception {

            var request = EnrollmentFactory.enrollmentRequest();
            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.create(any()))
                    .thenReturn(response);

            mockMvc.perform(post("/enrollments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id()));

            verify(enrollmentService).create(any());
        }

        @Test
        void shouldReturnConflictWhenDatabaseError() throws Exception {

            var request = EnrollmentFactory.enrollmentRequest();

            when(enrollmentService.create(any()))
                    .thenThrow(new DatabaseException("error"));

            mockMvc.perform(post("/enrollments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class GetById {

        @Test
        void shouldReturnEnrollment() throws Exception {

            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.getEnrollmentById(1L))
                    .thenReturn(response);

            mockMvc.perform(get("/enrollments/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()));

            verify(enrollmentService).getEnrollmentById(1L);
        }

        @Test
        void shouldReturnNotFound() throws Exception {

            when(enrollmentService.getEnrollmentById(1L))
                    .thenThrow(new ResourceNotFoundException("not found"));

            mockMvc.perform(get("/enrollments/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetAll {

        @Test
        void shouldReturnPage() throws Exception {

            var response = EnrollmentFactory.enrollmentSummaryResponse();

            var page = new PageImpl<>(List.of(response));

            when(enrollmentService.getEnrollments(any()))
                    .thenReturn(page);

            mockMvc.perform(get("/enrollments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id")
                            .value(response.id()));

            verify(enrollmentService).getEnrollments(any());
        }
    }

     @Nested
    class Update {

        @Test
        void shouldUpdate() throws Exception {

            var request = EnrollmentFactory.updateRequest();
            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.update(eq(1L), any()))
                    .thenReturn(response);

            mockMvc.perform(patch("/enrollments/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(enrollmentService).update(eq(1L), any());
        }

        @Test
        void shouldReturnNotFound() throws Exception {

            var request = EnrollmentFactory.updateRequest();

            when(enrollmentService.update(eq(1L), any()))
                    .thenThrow(new ResourceNotFoundException("x"));

            mockMvc.perform(patch("/enrollments/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class Cancel {

        @Test
        void shouldCancel() throws Exception {

            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.cancel(1L))
                    .thenReturn(response);

            mockMvc.perform(patch("/enrollments/1/cancel"))
                    .andExpect(status().isOk());

            verify(enrollmentService).cancel(1L);
        }

        @Test
        void shouldReturnNotFound() throws Exception {

            when(enrollmentService.cancel(1L))
                    .thenThrow(new ResourceNotFoundException("x"));

            mockMvc.perform(patch("/enrollments/1/cancel"))
                    .andExpect(status().isNotFound());
        }
    }

     @Nested
    class Complete {

        @Test
        void shouldComplete() throws Exception {

            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.complete(1L))
                    .thenReturn(response);

            mockMvc.perform(patch("/enrollments/1/complete"))
                    .andExpect(status().isOk());

            verify(enrollmentService).complete(1L);
        }
    }

    @Nested
    class AddPayment {

        @Test
        void shouldAddPayment() throws Exception {

            var request = EnrollmentFactory.paymentRequest();
            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.addPayment(eq(1L), any()))
                    .thenReturn(response);

            mockMvc.perform(post("/enrollments/1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(enrollmentService).addPayment(eq(1L), any());
        }
    }

    @Nested
    class UpdatePayment {

        @Test
        void shouldUpdatePayment() throws Exception {

            var request = EnrollmentFactory.updatePaymentRequest();
            var response = EnrollmentFactory.enrollmentResponse();

            when(enrollmentService.updatePayment(eq(1L), eq(1L), any()))
                    .thenReturn(response);

            mockMvc.perform(patch("/enrollments/1/payments/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(enrollmentService)
                    .updatePayment(eq(1L), eq(1L), any());
        }
    }
}
