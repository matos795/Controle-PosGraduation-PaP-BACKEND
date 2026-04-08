package com.pregueapalavra.posGraduationControl.packages.student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pregueapalavra.posGraduationControl.packages.student.dto.CreateStudentRequest;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentProgressResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.StudentResponse;
import com.pregueapalavra.posGraduationControl.packages.student.dto.UpdateStudentRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse createStudent(@Valid @RequestBody CreateStudentRequest requestDTO) {
        return studentService.createStudent(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> getStudents(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String sort,
        Pageable pageable) {
        return studentService.getStudents(name, status, sort, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse updateStudent(@PathVariable Long id, @Valid @RequestBody UpdateStudentRequest requestDTO) {
        return studentService.updateStudent(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudents(@RequestBody List<Long> ids) {
        studentService.deleteStudent(ids);
    }

    @GetMapping("/{id}/progress")
    public StudentProgressResponse getProgress(@PathVariable Long id) {
        return studentService.getStudentProgress(id);
    }
}
