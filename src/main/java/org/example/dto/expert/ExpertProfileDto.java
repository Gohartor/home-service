package org.example.dto.expert;

import java.util.List;

public record ExpertProfileDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        List<String> serviceNames,
        String bio,
        String profilePhotoUrl,
        String expertStatus
) {}
