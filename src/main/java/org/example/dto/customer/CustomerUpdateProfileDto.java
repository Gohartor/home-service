package org.example.dto.customer;

import jakarta.validation.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

public record CustomerUpdateProfileDto(

        @Email(message = "Invalid email address")
        String email,

        String password
) {}
