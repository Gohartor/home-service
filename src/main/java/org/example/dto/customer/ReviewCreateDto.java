package org.example.dto.customer;

public record ReviewCreateDto(
        Long orderId,
        Integer rating,
        String comment
) {}