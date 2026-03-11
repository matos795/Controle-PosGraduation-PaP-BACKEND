package com.pregueapalavra.posGraduationControl.packages.student;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.student.EmailAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.mapper.StudentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentResponse createStudent(CreateStudentRequest requestDTO) {
        if (studentRepository.existsByEmail(requestDTO.email())) {
            throw new EmailAlreadyExistsException("Email já está sendo usado!");
        }
        StudentEntity studentEntity = StudentMapper.toCreatedEntity(requestDTO);
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
    public Page<StudentResponse> getStudents(Pageable pageable) {
        Page<StudentEntity> pageStudents = studentRepository.findAll(pageable);
        return pageStudents.map(StudentMapper::toDTO);
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

    // ----------------------------------------------------------------------------

    private StudentEntity findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }
}
