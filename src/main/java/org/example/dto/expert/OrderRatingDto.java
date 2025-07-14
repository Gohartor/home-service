package org.example.dto.expert;

public record OrderRatingDto(
        Long orderId,
        Integer rating
) {}