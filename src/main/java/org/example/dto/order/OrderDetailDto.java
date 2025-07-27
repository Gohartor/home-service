package org.example.dto.order;

import java.time.ZonedDateTime;

public record OrderDetailDto(
        Long id,
        String description,
        String address,
        Double offeredPrice,
        Double totalPrice,
        String status,
        boolean paid,
        ZonedDateTime createdAt,
        ZonedDateTime expectedDoneAt,
        ZonedDateTime doneAt,
        String serviceName,
        String expertFullName,
        String customerFullName
) {}

