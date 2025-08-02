package org.example.dto.expert;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.entity.Service;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;

import java.util.HashSet;
import java.util.Set;

public record ExpertLoginResponseDto(

        String email,

        String firstName,

        String lastName,

        String profilePhoto,

        Long negativeScore,

        Double score,

        boolean isEmailVerified,

        Set<Service> services

) {
}

