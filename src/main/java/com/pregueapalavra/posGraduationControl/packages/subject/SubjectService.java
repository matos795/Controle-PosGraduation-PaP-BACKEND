package com.pregueapalavra.posGraduationControl.packages.subject;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<SubjectResponse> getSubjects() {
        List<SubjectEntity> pageSubjects = subjectRepository.findAll();
        return pageSubjects.stream().map(SubjectMapper::toDTO).toList();
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
    

    // ----------------------------------------------------------------------------

    private SubjectEntity findSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }
}