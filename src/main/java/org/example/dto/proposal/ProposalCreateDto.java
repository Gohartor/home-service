package org.example.dto.proposal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ProposalCreateDto(

        @NotNull(message = "OrderId is required")
        Long orderId,

        @NotNull(message = "Suggested price is required")
        @Positive(message = "Price must be positive")
        Integer suggestedPrice,

        @NotNull(message = "Suggested start time is required")
        LocalDateTime suggestedStartTime,

        @NotNull(message = "Estimated duration is required")
        @Positive(message = "Duration must be positive")
        Integer estimatedDuration

) {}
