package org.example.dto.expert;

import java.time.ZonedDateTime;

public record ExpertOrderDetailDto(
        Long orderId,
        String description,
        Double offeredPrice,
        String address,
        String serviceName,
        String status,
        String customerFullName,
        String customerEmail,
        String customerProfilePhoto,
        ZonedDateTime createdAt
) {}
