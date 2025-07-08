package org.example.service;

import org.example.entity.Proposal;

import java.util.List;
import java.util.Optional;

public interface ProposalService {
    Proposal save(Proposal entity);
    Optional<Proposal> findById(Long id);
    List<Proposal> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}