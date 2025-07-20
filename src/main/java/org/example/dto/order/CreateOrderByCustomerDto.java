package org.example.dto.order;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.ServiceStatus;

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



