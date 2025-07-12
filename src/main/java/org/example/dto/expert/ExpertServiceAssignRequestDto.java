package org.example.dto.expert;

import jakarta.validation.constraints.NotNull;

public record ExpertServiceAssignRequestDto(
        @NotNull(message = "Expert ID is required")
        Long expertId,

        @NotNull(message = "Service ID is required")
        Long serviceId
) {}
