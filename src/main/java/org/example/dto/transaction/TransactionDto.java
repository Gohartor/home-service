package org.example.dto.transaction;

import java.time.ZonedDateTime;

public record TransactionDto(
        Long id,
        Double amount,
        String type,
        Long relatedOrderId,
        Long walletId,
        ZonedDateTime createdAt
) {}

