package org.example.dto.admin;

import org.example.entity.enumerator.RoleType;


public record UserSearchFilterDto(
        RoleType role,
        String firstname,
        String lastname,
        String email,
        String service,
        Double ratingFrom,
        Double ratingTo,
        Integer page,
        Integer size
) {}
