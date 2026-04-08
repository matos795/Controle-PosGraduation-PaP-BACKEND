package com.pregueapalavra.posGraduationControl.student;

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
import com.pregueapalavra.posGraduationControl.packages.student.StudentEntity;
import com.pregueapalavra.posGraduationControl.packages.student.StudentRepository;
import com.pregueapalavra.posGraduationControl.packages.student.StudentService;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;
import com.pregueapalavra.posGraduationControl.student.factory.CreateStudentTestFactory;
import com.pregueapalavra.posGraduationControl.student.factory.UpdateStudentTestFactory;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Nested
    class CreateStudent {

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {

            // Arrange
            var request = CreateStudentTestFactory.createRequest();

            when(studentRepository.existsByEmail(request.email())).thenReturn(true);

            // Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () -> studentService.createStudent(request));

            verify(studentRepository).existsByEmail(request.email());
            verify(studentRepository, never()).save(any(StudentEntity.class));
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldCreateStudentWhenEmailDoesNotExist() {

            // Arrange
            var request = CreateStudentTestFactory.createRequest();

            when(studentRepository.existsByEmail(request.email())).thenReturn(false);
            when(studentRepository.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            StudentResponse response = studentService.createStudent(request);

            // Assert
            assertNotNull(response);
            assertEquals(request.email(), response.email());
            assertEquals(request.name(), response.name());

            verify(studentRepository).existsByEmail(request.email());
            verify(studentRepository).save(any(StudentEntity.class));
            verifyNoMoreInteractions(studentRepository);
        }
    }

    @Nested
    class UpdateStudent {

        @Test
        void shouldUpdateStudentWhenNewEmailIsAvailable() {

            // Arrange
            var request = UpdateStudentTestFactory.updateRequest();
            var entity = UpdateStudentTestFactory.updateEntity();
            var id = entity.getId();

            when(studentRepository.findById(id)).thenReturn(Optional.of(entity));
            when(studentRepository.existsByEmail(request.email())).thenReturn(false);
            when(studentRepository.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            StudentResponse response = studentService.updateStudent(id, request);

            // Assert
            assertNotNull(response);
            assertEquals(request.email(), response.email());
            assertEquals(request.name(), response.name());

            verify(studentRepository).findById(id);
            verify(studentRepository).existsByEmail(request.email());
            verify(studentRepository).save(any(StudentEntity.class));
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldThrowExceptionWhenStudentNotFound() {

            // Arrange
            var request = UpdateStudentTestFactory.updateRequest();
            var entity = UpdateStudentTestFactory.updateEntity();
            var id = entity.getId();

            when(studentRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> studentService.updateStudent(id, request));

            verify(studentRepository).findById(id);
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {

            // Arrange
            var request = UpdateStudentTestFactory.updateRequest();
            var entity = UpdateStudentTestFactory.updateEntity();
            var id = entity.getId();

            when(studentRepository.findById(id)).thenReturn(Optional.of(entity));
            when(studentRepository.existsByEmail(request.email())).thenReturn(true);

            // Act & Assert
            assertThrows(EmailAlreadyExistsException.class,
                    () -> studentService.updateStudent(id, request));

            verify(studentRepository).findById(id);
            verify(studentRepository).existsByEmail(request.email());
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldUpdateStudentWhenEmailIsUnchanged() {

            // Arrange
            var entity = UpdateStudentTestFactory.updateEntity();
            var id = entity.getId();

            var request = new UpdateStudentRequest(
                    entity.getName(),
                    entity.getEmail(), // mesmo email
                    entity.getPhone(),
                    entity.getAddress());

            when(studentRepository.findById(id)).thenReturn(Optional.of(entity));
            when(studentRepository.save(any(StudentEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            studentService.updateStudent(id, request);

            // Assert
            verify(studentRepository).findById(id);
            verify(studentRepository, never()).existsByEmail(any());
            verify(studentRepository).save(any(StudentEntity.class));
            verifyNoMoreInteractions(studentRepository);
        }
    }

    @Nested
    class GetStudentById {

        @Test
        void shouldReturnStudentWhenFound() {

            // Arrange
            var entity = CreateStudentTestFactory.createEntity();
            var id = entity.getId();

            when(studentRepository.findById(id)).thenReturn(Optional.of(entity));

            // Act
            StudentResponse response = studentService.getStudentById(id);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getEmail(), response.email());
            assertEquals(entity.getName(), response.name());

            verify(studentRepository).findById(id);
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldThrowExceptionWhenStudentNotFound() {

            // Arrange
            var id = 1L;

            when(studentRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(id));

            verify(studentRepository).findById(id);
            verifyNoMoreInteractions(studentRepository);
        }
    }

    @Nested
    class GetStudents {

        @Test
        void shouldReturnPagedStudents() {

            // Arrange
            var entity = CreateStudentTestFactory.createEntity();

            var pageable = PageRequest.of(0, 10);

            Page<StudentEntity> page = new PageImpl<>(List.of(entity));

            when(studentRepository.findAll(pageable)).thenReturn(page);

            // Act
            Page<StudentResponse> response = studentService.getStudents(null, null, null, pageable);

            // Assert
            assertNotNull(response);
            assertEquals(page.getTotalElements(), response.getTotalElements());
            assertEquals(page.getTotalPages(), response.getTotalPages());
            assertEquals(page.getContent().size(), response.getContent().size());

            var student = response.getContent().get(0);
            assertEquals(entity.getId(), student.id());
            assertEquals(entity.getEmail(), student.email());
            assertEquals(entity.getName(), student.name());

            verify(studentRepository).findAll(pageable);
            verifyNoMoreInteractions(studentRepository);
        }
    }

    @Nested
    class DeleteStudent {

        @Test
        void shouldDeleteStudentWhenFound() {

            // Arrange
            var entity = CreateStudentTestFactory.createEntity();
            var id = entity.getId();

            when(studentRepository.existsById(id)).thenReturn(true);

            // Act
            studentService.deleteStudent(id);

            // Assert
            verify(studentRepository).existsById(id);
            verify(studentRepository).deleteById(id);
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldThrowExceptionWhenStudentNotFound() {

            // Arrange
            var id = 1L;

            when(studentRepository.existsById(id)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> studentService.deleteStudent(id));

            verify(studentRepository).existsById(id);
            verify(studentRepository, never()).deleteById(id);
            verifyNoMoreInteractions(studentRepository);
        }

        @Test
        void shouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {

            // Arrange
            var id = 1L;

            when(studentRepository.existsById(id)).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(studentRepository).deleteById(id);

            // Act & Assert
            assertThrows(DatabaseException.class, () -> studentService.deleteStudent(id));

            verify(studentRepository).existsById(id);
            verify(studentRepository).deleteById(id);
            verifyNoMoreInteractions(studentRepository);
        }
    }
}
