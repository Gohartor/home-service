package org.example.dto.order;

import java.time.ZonedDateTime;

public record OrderSummaryDto(
        Long id,
        String serviceName,
        String status,
        String expertFullName,
        String customerFullName,
        ZonedDateTime createdAt,
        Double totalPrice,
        boolean paid,
        ZonedDateTime doneAt
) {}

