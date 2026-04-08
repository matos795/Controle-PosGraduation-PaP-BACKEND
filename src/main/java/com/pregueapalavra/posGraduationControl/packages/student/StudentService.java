package com.pregueapalavra.posGraduationControl.packages.student;

import java.util.List;

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
import com.pregueapalavra.posGraduationControl.packages.enrollment.EnrollmentRepository;
import com.pregueapalavra.posGraduationControl.packages.enrollment.enums.EnrollmentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentProgressResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.enums.StudentStatus;
import com.pregueapalavra.posGraduationControl.packages.student.mapper.StudentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    private final EnrollmentRepository enrollmentRepository;

    public StudentResponse createStudent(CreateStudentRequest requestDTO) {
        if (studentRepository.existsByEmail(requestDTO.email())) {
            throw new EmailAlreadyExistsException("Email já está sendo usado!");
        }
        StudentEntity studentEntity = StudentMapper.toCreatedEntity(requestDTO);
        studentEntity.setStatus(StudentStatus.IN_PROGRESS);
        StudentEntity savedStudent = studentRepository.save(studentEntity);
        return StudentMapper.toDTO(savedStudent);
    }

    public StudentResponse updateStudent(Long id, UpdateStudentRequest requestDTO) {
        StudentEntity studentEntity = findStudentById(id);
        if (requestDTO.email() != null && !requestDTO.email().equals(studentEntity.getEmail())) {
            if (studentRepository.existsByEmail(requestDTO.email())) {
                throw new EmailAlreadyExistsException("Email já está sendo usado!");
            }
        }
        StudentMapper.toUpdateEntity(studentEntity, requestDTO);
        StudentEntity updatedStudent = studentRepository.save(studentEntity);
        return StudentMapper.toDTO(updatedStudent);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(String name, String status, String sort, Pageable pageable) {

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
        boolean hasStatus = status != null && !status.isEmpty();

        if (hasName && hasStatus) {
            StudentStatus studentStatus = toStudentStatus(status);
            Page<StudentEntity> pageStudents = studentRepository.findByNameContainingIgnoreCaseAndStatus(name,
                    studentStatus, pageableWithSort);
            return pageStudents.map(StudentMapper::toDTO);
        }

        if (hasName) {
            Page<StudentEntity> pageStudents = studentRepository.findByNameContainingIgnoreCase(name, pageableWithSort);
            return pageStudents.map(StudentMapper::toDTO);
        }

        if (hasStatus) {
            StudentStatus studentStatus = toStudentStatus(status);
            Page<StudentEntity> pageStudents = studentRepository.findByStatus(studentStatus, pageableWithSort);
            return pageStudents.map(StudentMapper::toDTO);
        }

        Page<StudentEntity> pageStudents = studentRepository.findAll(pageableWithSort);
        return pageStudents.map(StudentMapper::toDTO);
    }

    private StudentStatus toStudentStatus(String status) {
        try {
            return StudentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid status: " + status);
        }
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        StudentEntity entity = findStudentById(id);
        return StudentMapper.toDTO(entity);
    }

    public void deleteStudent(Long id) {

        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }

        try {
            studentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting student with id: " + id);
        }
    }

    public void deleteStudent(List<Long> listId) {
        // Verify all students exist before deleting
        long existingCount = studentRepository.countByIdIn(listId);
        if (existingCount != listId.size()) {
            throw new ResourceNotFoundException("One or more students not found");
        }

        try {
            studentRepository.deleteAllById(listId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error deleting students due to data integrity constraints");
        }
    }

    // ----------------------------------------------------------------------------

    private StudentEntity findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public StudentProgressResponse getStudentProgress(Long studentId) {

        var enrollments = enrollmentRepository.findByStudentId(studentId);

        long completedSubjects = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .map(e -> e.getClassSession().getSubject().getId())
                .distinct()
                .count();

        long totalSubjects = 8;

        long remaining = totalSubjects - completedSubjects;

        boolean completed = completedSubjects >= totalSubjects;

        return new StudentProgressResponse(
                studentId,
                completedSubjects,
                totalSubjects,
                remaining,
                completed);
    }
}
