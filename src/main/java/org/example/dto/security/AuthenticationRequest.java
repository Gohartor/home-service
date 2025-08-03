package org.example.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record AuthenticationRequest(

        @Email(message = "format mail is incorrect")
        String email,

        @NotBlank
        String password
) {
}
