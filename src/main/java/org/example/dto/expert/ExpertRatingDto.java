package org.example.dto.expert;

public record ExpertRatingDto(
        double averageRating,
        int totalReviews
) {}