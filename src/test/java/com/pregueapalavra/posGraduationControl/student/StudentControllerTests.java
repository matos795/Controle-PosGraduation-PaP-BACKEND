package com.pregueapalavra.posGraduationControl.student;

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
import com.pregueapalavra.posGraduationControl.packages.student.StudentController;
import com.pregueapalavra.posGraduationControl.packages.student.StudentService;
import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;
import com.pregueapalavra.posGraduationControl.student.factory.CreateStudentTestFactory;
import com.pregueapalavra.posGraduationControl.student.factory.UpdateStudentTestFactory;

@WebMvcTest(StudentController.class)
public class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateStudent {

        @Test
        @DisplayName("POST /students")
        void shouldCreateStudent() throws Exception {

            // Arrange
            var request = CreateStudentTestFactory.createRequest();
            var response = CreateStudentTestFactory.createResponse();

            when(studentService.createStudent(any())).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(studentService).createStudent(any());
        }

        @Test
        @DisplayName("POST /students - Invalid Email")
        void shouldReturnUnprocessableEntityWhenEmailIsInvalid() throws Exception {

            // Arrange
            var request = new CreateStudentRequest("Alexandre", "email-invalido", null, null);

            // Act & Assert
            mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(studentService);
        }

        @Test
        @DisplayName("POST /students - Email already exists")
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

            var request = CreateStudentTestFactory.createRequest();

            when(studentService.createStudent(any()))
                    .thenThrow(new EmailAlreadyExistsException("Email already exists"));

            mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(studentService).createStudent(any());
        }
    }

    @Nested
    class GetStudentById {

        @Test
        @DisplayName("GET /students/{id}")
        void shouldReturnStudentWhenFound() throws Exception {

            // Arrange
            var response = CreateStudentTestFactory.createResponse();

            when(studentService.getStudentById(1L)).thenReturn(response);

            // Act & Assert
            mockMvc.perform(get("/students/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.email").value(response.email()));

            verify(studentService).getStudentById(1L);
        }

        @Test
        @DisplayName("GET /students/{id} - Not Found")
        void shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {

            // Arrange
            when(studentService.getStudentById(1L)).thenThrow(new ResourceNotFoundException("Student not found"));

            // Act & Assert
            mockMvc.perform(get("/students/1")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetStudents {

        @Test
        @DisplayName("GET /students")
        void shouldReturnPagedStudents() throws Exception {

            // Arrange
            var response = CreateStudentTestFactory.createResponse();
            var page = new PageImpl<>(List.of(response));

            when(studentService.getStudents("", "", any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get("/students"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].email").value(response.email()))
                    .andExpect(jsonPath("$.content[0].name").value(response.name()))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(studentService).getStudents("", "", any());
        }
    }

    @Nested
    class UpdateStudent {

        @Test
        @DisplayName("PUT /students/{id}")
        void shouldUpdateStudent() throws Exception {

            // Arrange
            var request = UpdateStudentTestFactory.updateRequest();
            var response = UpdateStudentTestFactory.updateResponse();

            when(studentService.updateStudent(eq(1L), any(UpdateStudentRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(put("/students/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(studentService).updateStudent(eq(1L), any(UpdateStudentRequest.class));
        }

        @Test
        @DisplayName("PUT /students/{id} - Invalid Email")
        void shouldReturnUnprocessableEntityWhenRequestIsInvalid() throws Exception {

            // Arrange
            var request = new UpdateStudentRequest("Alexandre", "email-invalido", null, null);

            // Act & Assert
            mockMvc.perform(put("/students/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(studentService);
        }

        @Test
        @DisplayName("PUT /students/{id} - Not Found")
        void shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {

            // Arrange
            var request = UpdateStudentTestFactory.updateRequest();

            when(studentService.updateStudent(eq(1L), any(UpdateStudentRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Student not found"));

            // Act & Assert
            mockMvc.perform(put("/students/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(studentService).updateStudent(eq(1L), any(UpdateStudentRequest.class));
        }

    }

    @Nested
    class DeleteStudent {

        @Test
        @DisplayName("DELETE /students/{id}")
        void shouldDeleteStudent() throws Exception {

            // Act & Assert
            mockMvc.perform(delete("/students/1"))
                    .andExpect(status().isNoContent());

            verify(studentService).deleteStudent(1L);
        }

        @Test
        @DisplayName("DELETE /students/{id} - Not Found")
        void shouldReturnNotFoundWhenDeletingNonExistingStudent() throws Exception {

            // Arrange
            doThrow(new ResourceNotFoundException("Student not found")).when(studentService).deleteStudent(1L);

            // Act & Assert
            mockMvc.perform(delete("/students/1"))
                    .andExpect(status().isNotFound());

            verify(studentService).deleteStudent(1L);
        }

        @Test
        @DisplayName("DELETE /students/{id} - Database Error")
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            // Arrange
            doThrow(new DatabaseException("Database error")).when(studentService).deleteStudent(1L);

            // Act & Assert
            mockMvc.perform(delete("/students/1"))
                    .andExpect(status().isConflict());

            verify(studentService).deleteStudent(1L);
        }
    }
}
