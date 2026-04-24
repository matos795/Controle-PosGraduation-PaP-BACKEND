package com.pregueapalavra.posGraduationControl.classSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import com.pregueapalavra.posGraduationControl.classSession.factory.CreateClassSessionTestFactory;
import com.pregueapalavra.posGraduationControl.classSession.factory.UpdateClassSessionTestFactory;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionEntity;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionRepository;
import com.pregueapalavra.posGraduationControl.packages.classSession.ClassSessionService;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectRepository;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherRepository;

@ExtendWith(MockitoExtension.class)
public class ClassSessionServiceTests {

    @InjectMocks
    private ClassSessionService classSessionService;

    @Mock
    private ClassSessionRepository classSessionRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Nested
    class CreateClassSession {

        @Test
        void shouldCreateClassSession() {

            // Arrange
            var request = CreateClassSessionTestFactory.createRequest();

            var teacher = new TeacherEntity();
            teacher.setId(request.teacherId());

            var subject = new SubjectEntity();
            subject.setId(request.subjectId());

            when(subjectRepository.findById(request.subjectId()))
                    .thenReturn(Optional.of(subject));

            when(teacherRepository.findById(request.teacherId()))
                    .thenReturn(Optional.of(teacher));

            when(classSessionRepository.save(any(ClassSessionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ClassSessionResponse response = classSessionService.createClassSession(request);

            // Assert
            assertNotNull(response);

            verify(subjectRepository).findById(request.subjectId());
            verify(teacherRepository).findById(request.teacherId());
            verify(classSessionRepository).save(any(ClassSessionEntity.class));
        }

        @Test
        void shouldThrowExceptionWhenTeacherNotFound() {

            // Arrange
            var request = CreateClassSessionTestFactory.createRequest();

            var teacher = new TeacherEntity();
            teacher.setId(request.teacherId());

            var subject = new SubjectEntity();
            subject.setId(request.subjectId());

            when(subjectRepository.findById(subject.getId())).thenReturn(Optional.of(subject));
            when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> classSessionService.createClassSession(request));

            verify(subjectRepository).findById(subject.getId());
            verify(teacherRepository).findById(teacher.getId());
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowExceptionWhenSubjectNotFound() {

            // Arrange
            var request = CreateClassSessionTestFactory.createRequest();

            var teacher = new TeacherEntity();
            teacher.setId(request.teacherId());

            var subject = new SubjectEntity();
            subject.setId(request.subjectId());

            when(subjectRepository.findById(subject.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> classSessionService.createClassSession(request));

            verify(subjectRepository).findById(subject.getId());
            verifyNoInteractions(teacherRepository);
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowExceptionWhenSaveFails() {

            var request = CreateClassSessionTestFactory.createRequest();

            var teacher = new TeacherEntity();
            teacher.setId(request.teacherId());

            var subject = new SubjectEntity();
            subject.setId(request.subjectId());

            when(subjectRepository.findById(request.subjectId()))
                    .thenReturn(Optional.of(subject));

            when(teacherRepository.findById(request.teacherId()))
                    .thenReturn(Optional.of(teacher));

            when(classSessionRepository.save(any()))
                    .thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class,
                    () -> classSessionService.createClassSession(request));

            verify(subjectRepository).findById(request.subjectId());
            verify(teacherRepository).findById(request.teacherId());
            verify(classSessionRepository).save(any(ClassSessionEntity.class));
        }
    }

    @Nested
    class UpdateClassSession {

        @Test
        void shouldUpdateClassSessionWhenDataIsValid() {

            // Arrange
            var request = UpdateClassSessionTestFactory.updateRequest();
            var entity = UpdateClassSessionTestFactory.updateEntity();
            var teacher = UpdateClassSessionTestFactory.teacher();

            var id = entity.getId();

            when(teacherRepository.findById(request.teacherId()))
                    .thenReturn(Optional.of(teacher));

            when(classSessionRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            when(classSessionRepository.save(any(ClassSessionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ClassSessionResponse response = classSessionService.updateClassSession(id, request);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());

            verify(teacherRepository).findById(request.teacherId());
            verify(classSessionRepository).findById(id);
            verify(classSessionRepository).save(any(ClassSessionEntity.class));
        }

        @Test
        void shouldThrowExceptionWhenClassSessionNotFound() {

            // Arrange
            var request = UpdateClassSessionTestFactory.updateRequest();

            when(teacherRepository.findById(request.teacherId()))
                    .thenReturn(Optional.of(UpdateClassSessionTestFactory.teacher()));
            when(classSessionRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> classSessionService.updateClassSession(1L, request));

            verify(teacherRepository).findById(request.teacherId());
            verify(classSessionRepository).findById(1L);
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowExceptionWhenTeacherNotFound() {

            // Arrange
            var request = UpdateClassSessionTestFactory.updateRequest();
            var entity = UpdateClassSessionTestFactory.updateEntity();
            var id = entity.getId();

            when(teacherRepository.findById(request.teacherId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> classSessionService.updateClassSession(id, request));

            verify(teacherRepository).findById(request.teacherId());
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowExceptionWhenSaveFails() {

            var request = UpdateClassSessionTestFactory.updateRequest();
            var entity = UpdateClassSessionTestFactory.updateEntity();
            var teacher = UpdateClassSessionTestFactory.teacher();

            var id = entity.getId();

            when(teacherRepository.findById(request.teacherId()))
                    .thenReturn(Optional.of(teacher));

            when(classSessionRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            when(classSessionRepository.save(any()))
                    .thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class,
                    () -> classSessionService.updateClassSession(id, request));

            verify(teacherRepository).findById(request.teacherId());
            verify(classSessionRepository).findById(id);
            verify(classSessionRepository).save(any(ClassSessionEntity.class));
        }
    }

    @Nested
    class GetClassSessionById {

        @Test
        void shouldReturnClassSessionWhenFound() {

            // Arrange
            var entity = CreateClassSessionTestFactory.createEntity();
            var id = entity.getId();

            when(classSessionRepository.findById(id)).thenReturn(Optional.of(entity));

            // Act
            ClassSessionResponse response = classSessionService.getClassSessionById(id);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getTitle(), response.title());
            assertEquals(entity.getTeacher().getId(), response.teacher().id());

            verify(classSessionRepository).findById(id);
        }

        @Test
        void shouldThrowExceptionWhenClassSessionNotFound() {

            // Arrange
            var id = 1L;

            when(classSessionRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> classSessionService.getClassSessionById(id));

            verify(classSessionRepository).findById(id);
            verifyNoMoreInteractions(classSessionRepository);
        }
    }

    @Nested
    class GetClassSessions {

    }

    @Nested
    class DeleteClassSession {

        @Test
        void shouldDeleteClassSessionWhenFound() {

            // Arrange
            var entity = CreateClassSessionTestFactory.createEntity();
            var id = entity.getId();

            when(classSessionRepository.existsById(id)).thenReturn(true);

            // Act
            classSessionService.deleteClassSession(id);

            // Assert
            verify(classSessionRepository).existsById(id);
            verify(classSessionRepository).deleteById(id);
        }

        @Test
        void shouldThrowExceptionWhenClassSessionNotFound() {

            // Arrange
            var id = 1L;

            when(classSessionRepository.existsById(id)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> classSessionService.deleteClassSession(id));

            verify(classSessionRepository).existsById(id);
            verify(classSessionRepository, never()).deleteById(id);
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {

            // Arrange
            var id = 1L;

            when(classSessionRepository.existsById(id)).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(classSessionRepository).deleteById(id);

            // Act & Assert
            assertThrows(DatabaseException.class, () -> classSessionService.deleteClassSession(id));

            verify(classSessionRepository).existsById(id);
            verify(classSessionRepository).deleteById(id);
            verifyNoMoreInteractions(classSessionRepository);
        }

        @Test
        void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() {

            var id = 1L;

            when(classSessionRepository.existsById(id)).thenReturn(true);

            doThrow(RuntimeException.class)
                    .when(classSessionRepository)
                    .deleteById(id);

            assertThrows(RuntimeException.class,
                    () -> classSessionService.deleteClassSession(id));

            verify(classSessionRepository).existsById(id);
            verify(classSessionRepository).deleteById(id);
        }
    }
}
