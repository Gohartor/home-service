package org.example.mapper;

import org.example.dto.proposal.ProposalRequestDTO;
import org.example.dto.proposal.ProposalResponseDTO;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;

@Mapper
public class ProposalMapper {

    public static Proposal toEntity(ProposalRequestDTO dto, Order order, User expert) {
        Proposal proposal = new Proposal();
        proposal.setProposedPrice(dto.getProposedPrice());
        proposal.setProposedStartAt(dto.getProposedStartAt());
        proposal.setDuration(dto.getDuration());
        proposal.setOrder(order);
        proposal.setExpert(expert);
        return proposal;
    }

    public static ProposalResponseDTO toDto(Proposal proposal) {
        ProposalResponseDTO dto = new ProposalResponseDTO();
        dto.setId(proposal.getId());
        dto.setProposedPrice(proposal.getProposedPrice());
        dto.setProposedStartAt(proposal.getProposedStartAt());
        dto.setDuration(proposal.getDuration());
        dto.setOrderId(proposal.getOrder().getId());
        dto.setExpertId(proposal.getExpert().getId());
        return dto;
    }
}