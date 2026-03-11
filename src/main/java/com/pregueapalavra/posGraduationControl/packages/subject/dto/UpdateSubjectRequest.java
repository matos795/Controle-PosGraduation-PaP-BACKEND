package com.pregueapalavra.posGraduationControl.packages.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSubjectRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    String name
) {}
