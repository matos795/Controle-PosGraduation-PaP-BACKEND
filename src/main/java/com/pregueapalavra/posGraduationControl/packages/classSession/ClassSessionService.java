package com.pregueapalavra.posGraduationControl.packages.classSession;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionResponse;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.CreateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.UpdateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.classSession.mapper.ClassSessionMapper;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectEntity;
import com.pregueapalavra.posGraduationControl.packages.subject.SubjectRepository;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherEntity;
import com.pregueapalavra.posGraduationControl.packages.teacher.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public ClassSessionResponse createClassSession(CreateClassSessionRequest requestDTO) {

        SubjectEntity subject = subjectRepository
                .findById(requestDTO.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        TeacherEntity teacher = teacherRepository
                .findById(requestDTO.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        validateDates(requestDTO.initialDate(), requestDTO.finalDate());

        ClassSessionEntity classSessionEntity = ClassSessionMapper.toCreatedEntity(requestDTO, teacher, subject);
        ClassSessionEntity savedClassSession = classSessionRepository.save(classSessionEntity);
        return ClassSessionMapper.toDTO(savedClassSession);
    }

    public ClassSessionResponse updateClassSession(Long id, UpdateClassSessionRequest requestDTO) {

        TeacherEntity teacher = null;
        SubjectEntity subject = null;

        if (requestDTO.teacherId() != null) {
            teacher = teacherRepository
                    .findById(requestDTO.teacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        }

        if (requestDTO.subjectId() != null) {
            subject = subjectRepository
                    .findById(requestDTO.subjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        }

        ClassSessionEntity classSessionEntity = findClassSessionById(id);

        LocalDate initial = requestDTO.initialDate() != null
                ? requestDTO.initialDate()
                : classSessionEntity.getInitialDate();

        LocalDate end = requestDTO.finalDate() != null
                ? requestDTO.finalDate()
                : classSessionEntity.getFinalDate();

        validateDates(initial, end);

        ClassSessionMapper.toUpdateEntity(classSessionEntity, requestDTO, teacher, subject);
        ClassSessionEntity updatedClassSession = classSessionRepository.save(classSessionEntity);
        return ClassSessionMapper.toDTO(updatedClassSession);
    }

    @Transactional(readOnly = true)
    public Page<ClassSessionResponse> getClassSessions(
            String name, Long subjectId, LocalDate start, LocalDate end, Integer year, Pageable pageable) {

        Specification<ClassSessionEntity> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + name.toLowerCase() + "%"));
        }

        if (subjectId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("subject").get("id"), subjectId));
        }

        if (start != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("initialDate"), start));
        }

        if (end != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("initialDate"), end));
        }

        if (year != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("year", Integer.class, root.get("initialDate")), year));
        }

        return classSessionRepository.findAll(spec, pageable)
                .map(ClassSessionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ClassSessionResponse getClassSessionById(Long id) {
        ClassSessionEntity entity = findClassSessionById(id);
        return ClassSessionMapper.toDTO(entity);
    }

    public void deleteClassSession(Long id) {

        if (!classSessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class session not found with id: " + id);
        }

        try {
            classSessionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting class session with id: " + id);
        }
    }

    public void deleteClassSession(List<Long> listId) {
        // Verify all class sessions exist before deleting
        long existingCount = classSessionRepository.countByIdIn(listId);
        if (existingCount != listId.size()) {
            throw new ResourceNotFoundException("One or more class sessions not found");
        }

        try {
            classSessionRepository.deleteAllById(listId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting class sessions due to data integrity constraints");
        }
    }

    // ----------------------------------------------------------------------------

    private ClassSessionEntity findClassSessionById(Long id) {
        return classSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class session not found with id: " + id));
    }

    private void validateDates(LocalDate initial, LocalDate end) {
        if (initial != null && end != null && end.isBefore(initial)) {
            throw new IllegalArgumentException("Final date cannot be before initial date");
        }
    }
}
