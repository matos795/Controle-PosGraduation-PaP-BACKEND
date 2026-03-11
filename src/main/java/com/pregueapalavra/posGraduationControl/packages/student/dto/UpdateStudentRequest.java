package com.pregueapalavra.posGraduationControl.packages.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateStudentRequest(
        @Size(max = 100, message = "Name is too long")
        String name,
        @Email(message = "Email is invalid")
        @Size(max = 150, message = "Email is too long")
        String email,
        @Size(max = 20, message = "Phone is too long")
        String phone,
        @Size(max = 200, message = "Address is too long")
        String address
) {}
