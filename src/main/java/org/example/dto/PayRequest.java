package org.example.dto;

public record PayRequest(
        String token,
        String captcha,
        String cardNumber,
        String expiryDate,
        String cvv
) {
}
