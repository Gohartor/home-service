package org.example.dto.payment;

public record PayRequest(
        String token,
        String captcha
) {
}
