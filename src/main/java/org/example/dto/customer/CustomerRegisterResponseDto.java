package org.example.dto.customer;

import java.util.List;

public record CustomerRegisterResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email) {
}
