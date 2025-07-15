package org.example.dto.wallet;

public record WalletChargeDto(
        Double amount,
        String cardNumber,
        String cvv2,
        String expDate,
        String secondPassword,
        String captchaCode
) {}

