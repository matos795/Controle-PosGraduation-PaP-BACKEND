package com.pregueapalavra.posGraduationControl.teacher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.student.EmailAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherController;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherService;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.CreateTeacherRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.UpdateTeacherRequest;
import com.pregueapalavra.posGraduationControl.teacher.factory.CreateTeacherTestFactory;
import com.pregueapalavra.posGraduationControl.teacher.factory.UpdateTeacherTestFactory;

@WebMvcTest(TeacherController.class)
public class TeacherControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateTeacher {

        @Test
        @DisplayName("POST /teachers")
        void shouldCreateTeacher() throws Exception {

            // Arrange
            var request = CreateTeacherTestFactory.createRequest();
            var response = CreateTeacherTestFactory.createResponse();

            when(teacherService.createTeacher(any())).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/teachers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(teacherService).createTeacher(any());
        }

        @Test
        @DisplayName("POST /teachers - Invalid Email")
        void shouldReturnUnprocessableEntityWhenEmailIsInvalid() throws Exception {

            // Arrange
            var request = new CreateTeacherRequest("Alexandre", "email-invalido", null, null);

            // Act & Assert
            mockMvc.perform(post("/teachers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(teacherService);
        }

        @Test
        @DisplayName("POST /teachers - Email already exists")
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

            var request = CreateTeacherTestFactory.createRequest();

            when(teacherService.createTeacher(any()))
                    .thenThrow(new EmailAlreadyExistsException("Email already exists"));

            mockMvc.perform(post("/teachers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(teacherService).createTeacher(any());
        }
    }

    @Nested
    class GetTeacherById {

        @Test
        @DisplayName("GET /teachers/{id}")
        void shouldReturnTeacherWhenFound() throws Exception {

            // Arrange
            var response = CreateTeacherTestFactory.createResponse();

            when(teacherService.getTeacherById(1L)).thenReturn(response);

            // Act & Assert
            mockMvc.perform(get("/teachers/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.email").value(response.email()));

            verify(teacherService).getTeacherById(1L);
        }

        @Test
        @DisplayName("GET /teachers/{id} - Not Found")
        void shouldReturnNotFoundWhenTeacherDoesNotExist() throws Exception {

            // Arrange
            when(teacherService.getTeacherById(1L)).thenThrow(new ResourceNotFoundException("Teacher not found"));

            // Act & Assert
            mockMvc.perform(get("/teachers/1")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetTeachers {

    }

    @Nested
    class UpdateTeacher {

        @Test
        @DisplayName("PUT /teachers/{id}")
        void shouldUpdateTeacher() throws Exception {

            // Arrange
            var request = UpdateTeacherTestFactory.updateRequest();
            var response = UpdateTeacherTestFactory.updateResponse();

            when(teacherService.updateTeacher(eq(1L), any(UpdateTeacherRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(put("/teachers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(teacherService).updateTeacher(eq(1L), any(UpdateTeacherRequest.class));
        }

        @Test
        @DisplayName("PUT /teachers/{id} - Invalid Email")
        void shouldReturnUnprocessableEntityWhenRequestIsInvalid() throws Exception {

            // Arrange
            var request = new UpdateTeacherRequest("Alexandre", "email-invalido", null, null);

            // Act & Assert
            mockMvc.perform(put("/teachers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(teacherService);
        }

        @Test
        @DisplayName("PUT /teachers/{id} - Not Found")
        void shouldReturnNotFoundWhenTeacherDoesNotExist() throws Exception {

            // Arrange
            var request = UpdateTeacherTestFactory.updateRequest();

            when(teacherService.updateTeacher(eq(1L), any(UpdateTeacherRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Teacher not found"));

            // Act & Assert
            mockMvc.perform(put("/teachers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(teacherService).updateTeacher(eq(1L), any(UpdateTeacherRequest.class));
        }

    }

    @Nested
    class DeleteTeacher {

        @Test
        @DisplayName("DELETE /teachers/{id}")
        void shouldDeleteTeacher() throws Exception {

            // Act & Assert
            mockMvc.perform(delete("/teachers/1"))
                    .andExpect(status().isNoContent());

            verify(teacherService).deleteTeacher(1L);
        }

        @Test
        @DisplayName("DELETE /teachers/{id} - Not Found")
        void shouldReturnNotFoundWhenDeletingNonExistingTeacher() throws Exception {

            // Arrange
            doThrow(new ResourceNotFoundException("Teacher not found")).when(teacherService).deleteTeacher(1L);

            // Act & Assert
            mockMvc.perform(delete("/teachers/1"))
                    .andExpect(status().isNotFound());

            verify(teacherService).deleteTeacher(1L);
        }

        @Test
        @DisplayName("DELETE /teachers/{id} - Database Error")
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            // Arrange
            doThrow(new DatabaseException("Database error")).when(teacherService).deleteTeacher(1L);

            // Act & Assert
            mockMvc.perform(delete("/teachers/1"))
                    .andExpect(status().isConflict());

            verify(teacherService).deleteTeacher(1L);
        }
    }
}
