package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class ProposalRepositoryImpl
        extends BaseRepositoryImpl<Proposal, Long>
        implements ProposalRepository  {

    public ProposalRepositoryImpl() {
        this.domainClass = Proposal.class;
    }
}
