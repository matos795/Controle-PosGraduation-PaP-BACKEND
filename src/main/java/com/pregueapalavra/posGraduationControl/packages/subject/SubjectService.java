package com.pregueapalavra.posGraduationControl.packages.subject;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.subject.NameAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.mapper.SubjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectResponse createSubject(CreateSubjectRequest requestDTO) {
        if (subjectRepository.existsByName(requestDTO.name())) {
            throw new NameAlreadyExistsException("Subject name already exists: " + requestDTO.name());
        }
        SubjectEntity subjectEntity = SubjectMapper.toCreatedEntity(requestDTO);
        SubjectEntity savedSubject = subjectRepository.save(subjectEntity);
        return SubjectMapper.toDTO(savedSubject);
    }

    public SubjectResponse updateSubject(Long id, UpdateSubjectRequest requestDTO) {
        SubjectEntity subjectEntity = findSubjectById(id);
        if (requestDTO.name() != null && !requestDTO.name().equals(subjectEntity.getName())) {
            if (subjectRepository.existsByName(requestDTO.name())) {
                throw new NameAlreadyExistsException("Subject name already exists: " + requestDTO.name());
            }
        }
        SubjectMapper.toUpdateEntity(subjectEntity, requestDTO);
        SubjectEntity updatedSubject = subjectRepository.save(subjectEntity);
        return SubjectMapper.toDTO(updatedSubject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjects(String name, String sort, String sortDir) {

        boolean hasName = name != null && !name.trim().isEmpty();

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir != null ? sortDir : "DESC");
        } catch (Exception e) {
            direction = Sort.Direction.DESC;
        }

        String sortBy = (sort != null && !sort.isEmpty()) ? sort : "id";
        Sort sorting = Sort.by(direction, sortBy);

        List<SubjectEntity> subjects = hasName
                ? subjectRepository.findAllByNameContainingIgnoreCase(name, sorting)
                : subjectRepository.findAll(sorting);

        return subjects.stream()
                .map(SubjectMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long id) {
        SubjectEntity entity = findSubjectById(id);
        return SubjectMapper.toDTO(entity);
    }

    public void deleteSubject(Long id) {

        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        try {
            subjectRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting subject with id: " + id);
        }
    }

    public void deleteSubject(List<Long> listId) {
        // Verify all subjects exist before deleting
        long existingCount = subjectRepository.countByIdIn(listId);
        if (existingCount != listId.size()) {
            throw new ResourceNotFoundException("One or more subjects not found");
        }

        try {
            subjectRepository.deleteAllById(listId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting subjects due to data integrity constraints");
        }
    }
    

    // ----------------------------------------------------------------------------

    private SubjectEntity findSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }
}