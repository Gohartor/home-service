package org.example.dto.wallet;

public record WalletDto(
        Long id,
        Double balance,
        Long userId
) {}
