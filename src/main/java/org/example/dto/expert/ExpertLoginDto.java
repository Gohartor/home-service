package org.example.dto.expert;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ExpertLoginDto(
        @Email(message = "Invalid email address")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}

