package org.example.dto.order;

public record OrderRatingDto(
        Long orderId,
        Integer rating
) {}