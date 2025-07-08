package org.example.mapper;

import org.example.dto.proposal.ProposalRequestDto;
import org.example.dto.proposal.ProposalResponseDto;
import org.example.entity.Proposal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProposalMapper {

    Proposal toProposal(ProposalRequestDto dto);

    ProposalResponseDto toDto(Proposal proposal);

    List<ProposalResponseDto> toDtoList(List<Proposal> proposals);
}