package com.pregueapalavra.posGraduationControl.classSession;

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
import com.pregueapalavra.posGraduationControl.classSession.factory.CreateClassSessionTestFactory;
import com.pregueapalavra.posGraduationControl.classSession.factory.UpdateClassSessionTestFactory;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionController;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionService;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.CreateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.UpdateClassSessionRequest;

@WebMvcTest(ClassSessionController.class)
public class ClassSessionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClassSessionService classSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateClassSession {

        @Test
        @DisplayName("POST /class-sessions")
        void shouldCreateClassSession() throws Exception {

            // Arrange
            var request = CreateClassSessionTestFactory.createRequest();
            var response = CreateClassSessionTestFactory.createResponse();

            when(classSessionService.createClassSession(any())).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/class-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value(response.title()));

            verify(classSessionService).createClassSession(any());
        }

        @Test
        @DisplayName("POST /class-sessions - Invalid Request")
        void shouldReturnUnprocessableEntityWhenRequestIsInvalid() throws Exception {

            var request = new CreateClassSessionRequest(
                    null,
                    null,
                    null,
                    null,
                    null);

            mockMvc.perform(post("/class-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());

            verifyNoInteractions(classSessionService);
        }

        @Test
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            var request = CreateClassSessionTestFactory.createRequest();

            when(classSessionService.createClassSession(any()))
                    .thenThrow(new DatabaseException("error"));

            mockMvc.perform(post("/class-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class GetClassSessionById {

        @Test
        @DisplayName("GET /class-sessions/{id}")
        void shouldReturnClassSessionWhenFound() throws Exception {

            // Arrange
            var response = CreateClassSessionTestFactory.createResponse();

            when(classSessionService.getClassSessionById(1L)).thenReturn(response);

            // Act & Assert
            mockMvc.perform(get("/class-sessions/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.title").value(response.title()));

            verify(classSessionService).getClassSessionById(1L);
        }

        @Test
        @DisplayName("GET /class-sessions/{id} - Not Found")
        void shouldReturnNotFoundWhenClassSessionDoesNotExist() throws Exception {

            // Arrange
            when(classSessionService.getClassSessionById(1L))
                    .thenThrow(new ResourceNotFoundException("ClassSession not found"));

            // Act & Assert
            mockMvc.perform(get("/class-sessions/1")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetClassSessions {

        @Test
        @DisplayName("GET /class-sessions")
        void shouldReturnPagedClassSessions() throws Exception {

            // Arrange
            var response = CreateClassSessionTestFactory.createResponse();
            var page = new PageImpl<>(List.of(response));

            when(classSessionService.getClassSessions(any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get("/class-sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title").value(response.title()))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(classSessionService).getClassSessions(any());
        }

        @Test
        void shouldReturnEmptyPage() throws Exception {

            when(classSessionService.getClassSessions(any()))
                    .thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/class-sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());

            verify(classSessionService).getClassSessions(any());
        }
    }

    @Nested
    class UpdateClassSession {

        @Test
        @DisplayName("PUT /class-sessions/{id}")
        void shouldUpdateClassSession() throws Exception {

            // Arrange
            var request = UpdateClassSessionTestFactory.updateRequest();
            var response = UpdateClassSessionTestFactory.updateResponse();

            when(classSessionService.updateClassSession(eq(1L), any(UpdateClassSessionRequest.class)))
                    .thenReturn(response);

            // Act & Assert
            mockMvc.perform(put("/class-sessions/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(response.title()));

            verify(classSessionService).updateClassSession(eq(1L), any(UpdateClassSessionRequest.class));
        }

        @Test
        @DisplayName("PUT /class-sessions/{id} - Not Found")
        void shouldReturnNotFoundWhenClassSessionDoesNotExist() throws Exception {

            // Arrange
            var request = UpdateClassSessionTestFactory.updateRequest();

            when(classSessionService.updateClassSession(eq(1L), any(UpdateClassSessionRequest.class)))
                    .thenThrow(new ResourceNotFoundException("ClassSession not found"));

            // Act & Assert
            mockMvc.perform(put("/class-sessions/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(classSessionService).updateClassSession(eq(1L), any(UpdateClassSessionRequest.class));
        }

        @Test
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            var request = UpdateClassSessionTestFactory.updateRequest();

            when(classSessionService.updateClassSession(eq(1L), any()))
                    .thenThrow(new DatabaseException("error"));

            mockMvc.perform(put("/class-sessions/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class DeleteClassSession {

        @Test
        @DisplayName("DELETE /class-sessions/{id}")
        void shouldDeleteClassSession() throws Exception {

            // Act & Assert
            mockMvc.perform(delete("/class-sessions/1"))
                    .andExpect(status().isNoContent());

            verify(classSessionService).deleteClassSession(1L);
        }

        @Test
        @DisplayName("DELETE /class-sessions/{id} - Not Found")
        void shouldReturnNotFoundWhenDeletingNonExistingClassSession() throws Exception {

            // Arrange
            doThrow(new ResourceNotFoundException("ClassSession not found")).when(classSessionService)
                    .deleteClassSession(1L);

            // Act & Assert
            mockMvc.perform(delete("/class-sessions/1"))
                    .andExpect(status().isNotFound());

            verify(classSessionService).deleteClassSession(1L);
        }

        @Test
        @DisplayName("DELETE /class-sessions/{id} - Database Error")
        void shouldReturnConflictWhenDatabaseErrorOccurs() throws Exception {

            // Arrange
            doThrow(new DatabaseException("Database error")).when(classSessionService).deleteClassSession(1L);

            // Act & Assert
            mockMvc.perform(delete("/class-sessions/1"))
                    .andExpect(status().isConflict());

            verify(classSessionService).deleteClassSession(1L);
        }
    }
}
