package com.pregueapalavra.posGraduationControl.teacher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.student.EmailAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherRepository;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherService;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherResponse;
import com.pregueapalavra.posGraduationControl.teacher.factory.CreateTeacherTestFactory;
import com.pregueapalavra.posGraduationControl.teacher.factory.UpdateTeacherTestFactory;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTests {

    
    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Nested
    class CreateTeacher {

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {

            // Arrange
            var request = CreateTeacherTestFactory.createRequest();

            when(teacherRepository.existsByEmail(request.email())).thenReturn(true);

            // Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () -> teacherService.createTeacher(request));

            verify(teacherRepository).existsByEmail(request.email());
            verify(teacherRepository, never()).save(any(TeacherEntity.class));
            verifyNoMoreInteractions(teacherRepository);
        }

        @Test
        void shouldCreateTeacherWhenEmailDoesNotExist() {

            // Arrange
            var request = CreateTeacherTestFactory.createRequest();

            when(teacherRepository.existsByEmail(request.email())).thenReturn(false);
            when(teacherRepository.save(any(TeacherEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            TeacherResponse response = teacherService.createTeacher(request);

            // Assert
            assertNotNull(response);
            assertEquals(request.name(), response.name());

            verify(teacherRepository).existsByEmail(request.email());
            verify(teacherRepository).save(any(TeacherEntity.class));
            verifyNoMoreInteractions(teacherRepository);
        }
    }

    @Nested
    class UpdateTeacher {

        @Test
        void shouldThrowExceptionWhenTeacherNotFound() {

            // Arrange
            var request = UpdateTeacherTestFactory.updateRequest();
            var entity = UpdateTeacherTestFactory.updateEntity();
            var id = entity.getId();

            when(teacherRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> teacherService.updateTeacher(id, request));

            verify(teacherRepository).findById(id);
            verifyNoMoreInteractions(teacherRepository);
        }

        @Test
        void shouldUpdateTeacherWhenEmailIsUnchanged() {

            // Arrange
            var entity = UpdateTeacherTestFactory.updateEntity();
            var id = entity.getId();

            var request = UpdateTeacherTestFactory.updateRequest();

            when(teacherRepository.findById(id)).thenReturn(Optional.of(entity));
            when(teacherRepository.save(any(TeacherEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            teacherService.updateTeacher(id, request);

            // Assert
            verify(teacherRepository).findById(id);
            verify(teacherRepository).save(any(TeacherEntity.class));
            verifyNoMoreInteractions(teacherRepository);
        }
    }

    @Nested
    class GetTeacherById {

        @Test
        void shouldReturnTeacherWhenFound() {

            // Arrange
            var entity = CreateTeacherTestFactory.createEntity();
            var id = entity.getId();

            when(teacherRepository.findById(id)).thenReturn(Optional.of(entity));

            // Act
            TeacherResponse response = teacherService.getTeacherById(id);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getEmail(), response.email());

            verify(teacherRepository).findById(id);
            verifyNoMoreInteractions(teacherRepository);
        }

        @Test
        void shouldThrowExceptionWhenTeacherNotFound() {

            // Arrange
            var id = 1L;

            when(teacherRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> teacherService.getTeacherById(id));

            verify(teacherRepository).findById(id);
            verifyNoMoreInteractions(teacherRepository);
        }
    }

    @Nested
    class GetTeachers {

    }

    @Nested
    class DeleteTeacher {

        @Test
        void shouldDeleteTeacherWhenFound() {

            // Arrange
            var entity = CreateTeacherTestFactory.createEntity();
            var id = entity.getId();

            when(teacherRepository.existsById(id)).thenReturn(true);

            // Act
            teacherService.deleteTeacher(id);

            // Assert
            verify(teacherRepository).existsById(id);
            verify(teacherRepository).deleteById(id);
            verifyNoMoreInteractions(teacherRepository);
        }

        @Test
        void shouldThrowExceptionWhenTeacherNotFound() {

            // Arrange
            var id = 1L;

            when(teacherRepository.existsById(id)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> teacherService.deleteTeacher(id));

            verify(teacherRepository).existsById(id);
            verify(teacherRepository, never()).deleteById(id);
            verifyNoMoreInteractions(teacherRepository);
        }

        @Test
        void shouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {

            // Arrange
            var id = 1L;

            when(teacherRepository.existsById(id)).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(teacherRepository).deleteById(id);

            // Act & Assert
            assertThrows(DatabaseException.class, () -> teacherService.deleteTeacher(id));

            verify(teacherRepository).existsById(id);
            verify(teacherRepository).deleteById(id);
            verifyNoMoreInteractions(teacherRepository);
        }
    }
}
