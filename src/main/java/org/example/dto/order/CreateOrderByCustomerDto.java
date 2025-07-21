package org.example.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.ZonedDateTime;

public record CreateOrderByCustomerDto(

        @NotNull
        Long customerId,

        @NotNull
        @Positive
        Integer offeredPrice,

        @NotNull
        ZonedDateTime expectedDoneAt,

        @NotNull
        @Positive
        Integer estimatedDuration,

        String description,

        @NotNull
        String address,

        @NotNull
        Long serviceId

) {}



