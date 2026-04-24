package com.pregueapalavra.posGraduationControl.packages.classSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pregueapalavra.posGraduationControl.packages.classSession.dto.ClassSessionResponse;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.CreateClassSessionRequest;
import com.pregueapalavra.posGraduationControl.packages.classSession.dto.UpdateClassSessionRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/class-sessions")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService classSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassSessionResponse createClassSession(@Valid @RequestBody CreateClassSessionRequest requestDTO) {
        return classSessionService.createClassSession(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ClassSessionResponse> getClassSessions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {
        return classSessionService.getClassSessions(name, subjectId, startDate, endDate, year, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClassSessionResponse getClassSessionById(@PathVariable Long id) {
        return classSessionService.getClassSessionById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClassSessionResponse updateClassSession(@PathVariable Long id, @Valid @RequestBody UpdateClassSessionRequest requestDTO) {
        return classSessionService.updateClassSession(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeachers(@RequestBody List<Long> ids) {
        classSessionService.deleteClassSession(ids);
    }
}
