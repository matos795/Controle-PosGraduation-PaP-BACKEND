package com.pregueapalavra.posGraduationControl.packages.teacher;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.student.EmailAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.CreateTeacherRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherResponse;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.UpdateTeacherRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.mapper.TeacherMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherResponse createTeacher(CreateTeacherRequest requestDTO) {
        if (teacherRepository.existsByEmail(requestDTO.email())) {
                throw new EmailAlreadyExistsException("Email já está sendo usado!");
            }
        TeacherEntity teacherEntity = TeacherMapper.toCreatedEntity(requestDTO);
        TeacherEntity savedTeacher = teacherRepository.save(teacherEntity);
        return TeacherMapper.toDTO(savedTeacher);
    }

    public TeacherResponse updateTeacher(Long id, UpdateTeacherRequest requestDTO) {
        TeacherEntity teacherEntity = findTeacherById(id);
        if (requestDTO.email() != null && !requestDTO.email().equals(teacherEntity.getEmail())) {
            if (teacherRepository.existsByEmail(requestDTO.email())) {
                throw new EmailAlreadyExistsException("Email já está sendo usado!");
            }
        }
        TeacherMapper.toUpdateEntity(teacherEntity, requestDTO);
        TeacherEntity updatedTeacher = teacherRepository.save(teacherEntity);
        return TeacherMapper.toDTO(updatedTeacher);
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> getTeachers(String name, String sort, Pageable pageable) {

        // Parse sort parameter
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.trim().isEmpty()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                try {
                    Sort.Direction direction = Sort.Direction.fromString(parts[1].trim().toUpperCase());
                    sortObj = Sort.by(direction, parts[0].trim());
                } catch (IllegalArgumentException e) {
                    // Invalid sort, ignore and use unsorted
                }
            }
        }

        // Create new Pageable with sort
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);
        boolean hasName = name != null && !name.isEmpty();

        if (hasName) {
            Page<TeacherEntity> page = teacherRepository.findByNameContainingIgnoreCase(name, pageableWithSort);
            return page.map(TeacherMapper::toDTO);
        }

        Page<TeacherEntity> pageTeachers = teacherRepository.findAll(pageableWithSort);
        return pageTeachers.map(TeacherMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        TeacherEntity entity = findTeacherById(id);
        return TeacherMapper.toDTO(entity);
    }

    public void deleteTeacher(Long id) {

        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }

        try {
            teacherRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting teacher with id: " + id);
        }
    }

    public void deleteTeacher(List<Long> listId) {
        // Verify all teachers exist before deleting
        long existingCount = teacherRepository.countByIdIn(listId);
        if (existingCount != listId.size()) {
            throw new ResourceNotFoundException("One or more teachers not found");
        }

        try {
            teacherRepository.deleteAllById(listId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting teachers due to data integrity constraints");
        }
    }

    // ----------------------------------------------------------------------------

    private TeacherEntity findTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }
}
