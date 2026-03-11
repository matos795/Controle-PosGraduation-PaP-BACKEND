package com.pregueapalavra.posGraduationControl.subjects;

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

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.subject.NameAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectRepository;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectService;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;
import com.pregueapalavra.posGraduationControl.subjects.factory.CreateSubjectTestFactory;
import com.pregueapalavra.posGraduationControl.subjects.factory.UpdateSubjectTestFactory;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTests {

    @InjectMocks
    private SubjectService subjectService;

    @Mock
    private SubjectRepository subjectRepository;

    @Nested
    class CreateSubject {

        @Test
        void shouldThrowExceptionWhenNameAlreadyExists() {

            // Arrange
            var request = CreateSubjectTestFactory.createRequest();

            when(subjectRepository.existsByName(request.name())).thenReturn(true);

            // Act & Assert
            assertThrows(NameAlreadyExistsException.class, () -> subjectService.createSubject(request));

            verify(subjectRepository).existsByName(request.name());
            verify(subjectRepository, never()).save(any(SubjectEntity.class));
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldCreateSubjectWhenNameDoesNotExist() {

            // Arrange
            var request = CreateSubjectTestFactory.createRequest();

            when(subjectRepository.existsByName(request.name())).thenReturn(false);
            when(subjectRepository.save(any(SubjectEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SubjectResponse response = subjectService.createSubject(request);

            // Assert
            assertNotNull(response);
            assertEquals(request.name(), response.name());

            verify(subjectRepository).existsByName(request.name());
            verify(subjectRepository).save(any(SubjectEntity.class));
            verifyNoMoreInteractions(subjectRepository);
        }
    }

    @Nested
    class UpdateSubject {

        @Test
        void shouldUpdateSubjectWhenNewNameIsAvailable() {

            // Arrange
            var request = UpdateSubjectTestFactory.updateRequest();
            var entity = UpdateSubjectTestFactory.updateEntity();
            var id = entity.getId();

            when(subjectRepository.findById(id)).thenReturn(Optional.of(entity));
            when(subjectRepository.existsByName(request.name())).thenReturn(false);
            when(subjectRepository.save(any(SubjectEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SubjectResponse response = subjectService.updateSubject(id, request);

            // Assert
            assertNotNull(response);
            assertEquals(request.name(), response.name());
            assertEquals(entity.getId(), response.id());

            verify(subjectRepository).findById(id);
            verify(subjectRepository).existsByName(request.name());
            verify(subjectRepository).save(any(SubjectEntity.class));
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldThrowExceptionWhenSubjectNotFound() {

            // Arrange
            var request = UpdateSubjectTestFactory.updateRequest();
            var entity = UpdateSubjectTestFactory.updateEntity();
            var id = entity.getId();

            when(subjectRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> subjectService.updateSubject(id, request));

            verify(subjectRepository).findById(id);
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldThrowExceptionWhenNameAlreadyExists() {

            // Arrange
            var request = UpdateSubjectTestFactory.updateRequest();
            var entity = UpdateSubjectTestFactory.updateEntity();
            var id = entity.getId();

            when(subjectRepository.findById(id)).thenReturn(Optional.of(entity));
            when(subjectRepository.existsByName(request.name())).thenReturn(true);

            // Act & Assert
            assertThrows(NameAlreadyExistsException.class,
                    () -> subjectService.updateSubject(id, request));

            verify(subjectRepository).findById(id);
            verify(subjectRepository).existsByName(request.name());
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldUpdateSubjectWhenNameIsUnchanged() {

            // Arrange
            var entity = UpdateSubjectTestFactory.updateEntity();
            var id = entity.getId();

            var request = new UpdateSubjectRequest(
                    entity.getName());

            when(subjectRepository.findById(id)).thenReturn(Optional.of(entity));
            when(subjectRepository.save(any(SubjectEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            subjectService.updateSubject(id, request);

            // Assert
            verify(subjectRepository).findById(id);
            verify(subjectRepository, never()).existsByName(any());
            verify(subjectRepository).save(any(SubjectEntity.class));
            verifyNoMoreInteractions(subjectRepository);
        }
    }

    @Nested
    class GetSubjectById {

        @Test
        void shouldReturnSubjectWhenFound() {

            // Arrange
            var entity = CreateSubjectTestFactory.createEntity();
            var id = entity.getId();

            when(subjectRepository.findById(id)).thenReturn(Optional.of(entity));

            // Act
            SubjectResponse response = subjectService.getSubjectById(id);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getName(), response.name());

            verify(subjectRepository).findById(id);
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldThrowExceptionWhenSubjectNotFound() {

            // Arrange
            var id = 1L;

            when(subjectRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> subjectService.getSubjectById(id));

            verify(subjectRepository).findById(id);
            verifyNoMoreInteractions(subjectRepository);
        }
    }

    @Nested
    class GetSubjects {

        @Test
        void shouldReturnListedSubjects() {

            // Arrange
            var entity = CreateSubjectTestFactory.createEntity();

            when(subjectRepository.findAll()).thenReturn(List.of(entity));

            // Act
            List<SubjectResponse> response = subjectService.getSubjects();

            // Assert
            assertNotNull(response);
            assertEquals(1, response.size());

            verify(subjectRepository).findAll();
            verifyNoMoreInteractions(subjectRepository);
        }
    }

    @Nested
    class DeleteSubject {

        @Test
        void shouldDeleteSubjectWhenFound() {

            // Arrange
            var entity = CreateSubjectTestFactory.createEntity();
            var id = entity.getId();

            when(subjectRepository.existsById(id)).thenReturn(true);

            // Act
            subjectService.deleteSubject(id);

            // Assert
            verify(subjectRepository).existsById(id);
            verify(subjectRepository).deleteById(id);
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldThrowExceptionWhenSubjectNotFound() {

            // Arrange
            var id = 1L;

            when(subjectRepository.existsById(id)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> subjectService.deleteSubject(id));

            verify(subjectRepository).existsById(id);
            verify(subjectRepository, never()).deleteById(id);
            verifyNoMoreInteractions(subjectRepository);
        }

        @Test
        void shouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {

            // Arrange
            var id = 1L;

            when(subjectRepository.existsById(id)).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(subjectRepository).deleteById(id);

            // Act & Assert
            assertThrows(DatabaseException.class, () -> subjectService.deleteSubject(id));

            verify(subjectRepository).existsById(id);
            verify(subjectRepository).deleteById(id);
            verifyNoMoreInteractions(subjectRepository);
        }
    }
}
