package org.example.dto;

public record PageCustom(
        int page,
        int size,
        String sortBy
) {
}
