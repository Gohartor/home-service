package org.example.mapper;

import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.dto.proposal.ProposalRequestDto;
import org.example.dto.proposal.ProposalResponseDto;
import org.example.dto.proposal.ProposalViewDto;
import org.example.entity.Proposal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProposalMapper {

    Proposal toProposal(ProposalRequestDto dto);

    ProposalResponseDto toDto(Proposal proposal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expert", ignore = true)
    @Mapping(target = "order", ignore = true)
    Proposal fromDto(ProposalCreateByExpertDto dto);


    @Mapping(target = "expertId", source = "expert.id")
    @Mapping(target = "firstName", source = "expert.firstName")
    @Mapping(target = "lastName", source = "expert.lastName")
    @Mapping(target = "expertScore", source = "expert.score")
    ProposalViewDto toViewDto(Proposal proposal);


    List<ProposalViewDto> toViewDtoList(List<Proposal> proposals);

}