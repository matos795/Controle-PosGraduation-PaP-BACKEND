package com.pregueapalavra.posGraduationControl.packages.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record CreateStudentRequest (

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 150)
    String email,

    @Size(max = 20, message = "Phone is too long")
    String phone,

    @Size(max = 200, message = "Address is too long")
    String address
) {}
