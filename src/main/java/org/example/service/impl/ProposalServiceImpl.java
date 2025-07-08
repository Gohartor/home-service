package org.example.service.impl;

import org.example.entity.Proposal;
import org.example.repository.ProposalRepository;
import org.example.service.ProposalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository repository;

    public ProposalServiceImpl(ProposalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Proposal save(Proposal entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Proposal> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Proposal> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}