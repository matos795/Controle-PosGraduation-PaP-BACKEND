package com.pregueapalavra.posGraduationControl.subjects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.subject.NameAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectController;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectService;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;
import com.pregueapalavra.posGraduationControl.subjects.factory.CreateSubjectTestFactory;
import com.pregueapalavra.posGraduationControl.subjects.factory.UpdateSubjectTestFactory;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(SubjectController.class)
public class SubjectControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubjectService subjectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateSubject {

        @Test
        @DisplayName("POST /subjects")
        void shouldCreateSubject() throws Exception {

            // Arrange
            var request = CreateSubjectTestFactory.createRequest();
            var response = CreateSubjectTestFactory.createResponse();

            when(subjectService.createSubject(any())).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(subjectService).createSubject(any());
        }

        @Test
        @DisplayName("POST /subjects - Name already exists")
        void shouldReturnConflictWhenNameAlreadyExists() throws Exception {

            // Arrange
            var request = CreateSubjectTestFactory.createRequest();

            when(subjectService.createSubject(any()))
                    .thenThrow(new NameAlreadyExistsException("Name already exists"));

            // Act & Assert
            mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(subjectService).createSubject(any());
        }

        @Test
        @DisplayName("POST /subjects - Invalid Name")
        void shouldReturnUnprocessableEntityWhenNameIsInvalid() throws Exception {

            // Arrange
            var request = new CreateSubjectRequest("", "");

            // Act & Assert
            mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(subjectService);
        }
    }

    @Nested
    class GetSubjectById {

        @Test
        @DisplayName("GET /subjects/{id}")
        void shouldReturnSubjectWhenFound() throws Exception {

            // Arrange
            var response = CreateSubjectTestFactory.createResponse();

            when(subjectService.getSubjectById(1L)).thenReturn(response);

            // Act & Assert
            mockMvc.perform(get("/subjects/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(subjectService).getSubjectById(1L);
        }

        @Test
        @DisplayName("GET /subjects/{id} - Not Found")
        void shouldReturnNotFoundWhenSubjectDoesNotExist() throws Exception {

            // Arrange
            when(subjectService.getSubjectById(1L)).thenThrow(new ResourceNotFoundException("Subject not found"));

            // Act & Assert
            mockMvc.perform(get("/subjects/1")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetSubjects {

    }

    @Nested
    class UpdateSubject {

        @Test
        @DisplayName("PUT /subjects/{id}")
        void shouldUpdateSubject() throws Exception {

            // Arrange
            var request = UpdateSubjectTestFactory.updateRequest();
            var response = UpdateSubjectTestFactory.updateResponse();

            when(subjectService.updateSubject(eq(1L), any(UpdateSubjectRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(put("/subjects/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(response.name()));

            verify(subjectService).updateSubject(eq(1L), any(UpdateSubjectRequest.class));
        }

        @Test
        @DisplayName("PUT /subjects/{id} - Not Found")
        void shouldReturnNotFoundWhenSubjectDoesNotExist() throws Exception {

            // Arrange
            var request = UpdateSubjectTestFactory.updateRequest();

            when(subjectService.updateSubject(eq(1L), any(UpdateSubjectRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Subject not found"));

            // Act & Assert
            mockMvc.perform(put("/subjects/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(subjectService).updateSubject(eq(1L), any(UpdateSubjectRequest.class));
        }

        @Test
        @DisplayName("PUT /subjects/{id} - Invalid Name")
        void shouldReturnUnprocessableEntityWhenNameIsInvalid() throws Exception {

            // Arrange
            var request = new UpdateSubjectRequest("", "");

            // Act & Assert
            mockMvc.perform(put("/subjects/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(subjectService);
        }
    }

    @Nested
    class DeleteSubject {

        @Test
        @DisplayName("DELETE /subjects/{id}")
        void shouldDeleteSubject() throws Exception {

            // Act & Assert
            mockMvc.perform(delete("/subjects/1"))
                    .andExpect(status().isNoContent());

            verify(subjectService).deleteSubject(1L);
        }

        @Test
        @DisplayName("DELETE /subjects/{id} - Not Found")
        void shouldReturnNotFoundWhenDeletingNonExistingSubject() throws Exception {

            // Arrange
            doThrow(new ResourceNotFoundException("Subject not found")).when(subjectService).deleteSubject(1L);

            // Act & Assert
            mockMvc.perform(delete("/subjects/1"))
                    .andExpect(status().isNotFound());

            verify(subjectService).deleteSubject(1L);
        }

        @Test
        @DisplayName("DELETE /subjects/{id} - Database Error")
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            // Arrange
            doThrow(new DatabaseException("Database error")).when(subjectService).deleteSubject(1L);

            // Act & Assert
            mockMvc.perform(delete("/subjects/1"))
                    .andExpect(status().isConflict());

            verify(subjectService).deleteSubject(1L);
        }
    }
}
