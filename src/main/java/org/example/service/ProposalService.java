package org.example.service;

import org.example.dto.proposal.ProposalCreateDto;
import org.example.entity.Proposal;

import java.util.List;
import java.util.Optional;

public interface ProposalService {
    Proposal save(Proposal entity);
    Optional<Proposal> findById(Long id);
    List<Proposal> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long countAllByOrder_Id(Long orderId);
    void submitProposal(Long expertId, ProposalCreateDto dto);
}