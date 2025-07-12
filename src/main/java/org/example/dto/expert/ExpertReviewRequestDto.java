package org.example.dto.expert;

import jakarta.validation.constraints.NotNull;

public record ExpertReviewRequestDto(
        @NotNull(message = "Expert ID is required")
        Long expertId
) {}
