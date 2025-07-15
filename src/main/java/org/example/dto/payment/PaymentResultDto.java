package org.example.dto.payment;

public record PaymentResultDto(
        Boolean success,
        String message
) {}
