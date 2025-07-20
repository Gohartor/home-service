package org.example.dto.expert;

import org.example.entity.enumerator.ExpertStatus;

public record ExpertResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        ExpertStatus expertStatus
) {}
