package org.example.dto.admin;

import java.util.List;

public record UserAdminListDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        List<String> services,
        Double rating
) {}