package org.example.dto.expert;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record ExpertUpdateProfileDto(

        @Email(message = "Invalid email address")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Mobile is required")
        String mobile,

        MultipartFile profilePhoto
) {}
