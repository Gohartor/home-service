package org.example.dto;

public record PaymentUrlResponse(
        String paymentUrl,
        String sessionToken
) {
}
