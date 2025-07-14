package org.example.dto.expert;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ExpertRegisterDto(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Password must contain letters and numbers"
        )
        String password,


        @NotEmpty(message = "At least one service must be selected")
        List<Long> serviceIds,


        @Size(max = 500, message = "Bio must not exceed 500 chars")
        String bio,

        @NotNull(message = "Profile image is required")
        MultipartFile profilePhoto
) {}
