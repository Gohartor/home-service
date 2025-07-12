package org.example.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServiceRequestDto(
        @NotBlank(message = "Service name must not be blank")
        String name,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be a positive number")
        Double basePrice,

        @NotBlank(message = "Description must not be blank")
        String description,

        Long parentId // Nullable for root services
) {}
