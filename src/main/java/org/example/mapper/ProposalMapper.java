package org.example.mapper;

import org.example.dto.proposal.ProposalCreateDto;
import org.example.dto.proposal.ProposalRequestDto;
import org.example.dto.proposal.ProposalResponseDto;
import org.example.entity.Proposal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProposalMapper {

    Proposal toProposal(ProposalRequestDto dto);

    ProposalResponseDto toDto(Proposal proposal);

    @Mapping(target = "expert", ignore = true)
    @Mapping(target = "order", ignore = true)
    Proposal fromDto(ProposalCreateDto dto);
    
}