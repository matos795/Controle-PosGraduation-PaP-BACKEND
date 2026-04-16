package com.pregueapalavra.posGraduationControl.packages.subject.dto;

public record SubjectResponse(
    Long id,
    String name,
    String description,
    Long classSessionCount
) {}
