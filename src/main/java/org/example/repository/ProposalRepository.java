package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.repository.base.BaseRepository;

import java.util.List;

public interface ProposalRepository
        extends BaseRepository<Proposal, Long> {

    List<Proposal> findByOrderId(Long orderId);

    long countAllByOrder_Id(Long orderId);

    boolean existsByExpertIdAndOrderId(Long expertId, Long orderId);

}
