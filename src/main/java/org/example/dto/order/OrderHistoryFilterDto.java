package org.example.dto.order;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record OrderHistoryFilterDto(
        @PastOrPresent(message = "startDate should be today or older")
        LocalDate startDate,
        @PastOrPresent(message = "endDate should be today or older")
        LocalDate endDate,

        @Pattern(
                regexp = "^(PENDING_PROPOSAL|PROPOSAL_SELECTED|EXPERT_ARRIVED|IN_PROGRESS|COMPLETED|CANCELED)?$",
                message = "invalid order status"
        )
        String status,

        @Size(max = 64, message = "too long name")
        String serviceName,

        @Positive(message = "expertId should be positive")
        Long expertId,

        @Positive(message = "customerId should be positive")
        Long customerId
) {}


