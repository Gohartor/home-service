package org.example.dto.service;

public record ServiceResponseDto(
        Long id,
        String name,
        Double basePrice,
        String description,
        Long parentId,
        String parentName
) {}
