package org.example.dto.payment;

import java.awt.image.BufferedImage;

public record PaymentInitResponse(
        String token,
        String captchaImageBase64
) {
}
