package org.example.dto.expert;

import java.time.ZonedDateTime;

public record ExpertOrderSummaryDto(
        Long orderId,
        String description,
        Double offeredPrice,
        String address,
        String serviceName,
        String status,
        String customerFullName,
        ZonedDateTime createdAt
) {}
