package com.pregueapalavra.posGraduationControl.packages.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pregueapalavra.posGraduationControl.packages.teacher.dto.CreateTeacherRequest;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.TeacherResponse;
import com.pregueapalavra.posGraduationControl.packages.teacher.dto.UpdateTeacherRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse createTeacher(@Valid @RequestBody CreateTeacherRequest requestDTO) {
        return teacherService.createTeacher(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> getTeachers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        return teacherService.getTeachers(name, sort, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse updateTeacher(@PathVariable Long id, @Valid @RequestBody UpdateTeacherRequest requestDTO) {
        return teacherService.updateTeacher(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeachers(@RequestBody List<Long> ids) {
        teacherService.deleteTeacher(ids);
    }
}
