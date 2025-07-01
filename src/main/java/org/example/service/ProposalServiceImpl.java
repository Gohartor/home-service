package org.example.service;

import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.repository.OrderRepository;
import org.example.repository.ProposalRepository;
import org.example.service.ProposalService;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProposalServiceImpl
        extends BaseServiceImpl<Proposal, Long, ProposalRepository>
        implements ProposalService {

    public ProposalServiceImpl(ProposalRepository repository) {
        super(repository);
    }



}