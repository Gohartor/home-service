package org.example.dto.expert;

import jakarta.validation.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

public record ExpertUpdateProfileDto(

        @Email(message = "Invalid email address")
        String email,

        String password,

        MultipartFile profilePhoto
) {}
