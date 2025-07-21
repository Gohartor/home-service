package org.example.dto.proposal;

public record ProposalViewDto(
    Long id,
    Long expertId,
    String firstName,
    String lastName,
    Integer expertScore,
    Double proposedPrice
){}
