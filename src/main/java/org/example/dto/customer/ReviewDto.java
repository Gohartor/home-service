package org.example.dto.customer;

import java.time.ZonedDateTime;

public record ReviewDto(
        Long id,
        Long orderId,
        Integer rating,
        String comment,
        String firstName,
        String lastName,
        ZonedDateTime createDate
) {}
