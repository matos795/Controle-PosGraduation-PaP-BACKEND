package com.pregueapalavra.posGraduationControl.packages.student.dto;

public record StudentProgressResponse(

        Long studentId,
        long completedSubjects,
        long totalSubjects,
        long remainingSubjects,
        boolean completed

) {}