package org.example.dto.expert;

import jakarta.validation.constraints.NotNull;
import org.example.entity.enumerator.ExpertStatus;

public record ExpertStatusUpdateDto(
        @NotNull(message = "Status is required")
        ExpertStatus status
) {}
