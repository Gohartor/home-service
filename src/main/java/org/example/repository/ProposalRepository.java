package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository
        extends BaseRepository<Proposal, Long> {

    List<Proposal> findByOrderId(Long orderId);

    long countAllByOrder_Id(Long orderId);

    boolean existsByExpertIdAndOrderId(Long expertId, Long orderId);


    Long countByOrder(Order order);

    Long countByOrder_Id(Long orderId);


    Optional<Proposal> findByOrderIdAndIsAcceptedTrue(Long orderId);

    @Query("select p from Proposal p where p.order.id = :orderId order by p.expert.score desc")
    List<Proposal> findByOrderIdOrderByExpertScoreDesc(@Param("orderId") Long orderId);

    @Query("select p from Proposal p where p.order.id = :orderId order by p.proposedPrice desc")
    List<Proposal> findByOrderIdOrderByProposedPriceDesc(@Param("orderId") Long orderId);

    List<Proposal> findByOrderIdOrderByProposedPriceAsc(Long orderId);




}
