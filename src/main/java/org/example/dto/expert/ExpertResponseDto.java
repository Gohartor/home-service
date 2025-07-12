package org.example.dto.expert;

public record ExpertResponseDto(
        Long id,
        String fullName,
        String email,
        String expertStatus

) {}
