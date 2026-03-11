package com.pregueapalavra.posGraduationControl.packages.subject;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pregueapalavra.posGraduationControl.packages.subject.dto.CreateSubjectRequest;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.SubjectResponse;
import com.pregueapalavra.posGraduationControl.packages.subject.dto.UpdateSubjectRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse createSubject(@Valid @RequestBody CreateSubjectRequest requestDTO) {
        return subjectService.createSubject(requestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubjectResponse> getSubjects() {
        return subjectService.getSubjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubjectResponse getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubjectResponse updateSubject(@PathVariable Long id, @Valid @RequestBody UpdateSubjectRequest requestDTO) {
        return subjectService.updateSubject(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}
