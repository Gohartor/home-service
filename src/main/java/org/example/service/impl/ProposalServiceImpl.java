package org.example.service.impl;

import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.dto.proposal.ProposalViewDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.entity.enumerator.OrderStatus;
import org.example.mapper.ProposalMapper;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository repository;
    private final OrderService orderService;
    private final UserService userService;
    private final ProposalMapper mapper;

    public ProposalServiceImpl(ProposalRepository repository, @Lazy OrderService orderService, UserService userService, ProposalMapper mapper, ProposalMapper proposalMapper) {
        this.repository = repository;
        this.orderService = orderService;
        this.userService = userService;
        this.mapper = mapper;
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

    public long countAllByOrder_Id(Long orderId){
        return repository.countAllByOrder_Id(orderId);
    }


    @Override
    @Transactional
    public void submitProposalByExpert(Long expertId, ProposalCreateByExpertDto dto) {

        if (repository.existsByExpertIdAndOrderId(expertId, dto.orderId())) {
            throw new IllegalStateException("You have already submitted a proposal for this order.");
        }

        Order order = orderService.findById(dto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!(order.getStatus() == OrderStatus.PENDING_PROPOSAL || order.getStatus() == OrderStatus.PROPOSAL_SELECTED)) {
            throw new IllegalStateException("Order is not open for proposals.");
        }

        User expert = userService.findById(expertId)
                .orElseThrow(() -> new IllegalArgumentException("Expert not found"));

        if (!expert.getServices().contains(order.getService())) {
            throw new AccessDeniedException("You are not allowed to submit a proposal for this order.");
        }

        Proposal proposal = mapper.fromDto(dto);
        System.out.println("Proposal ID before save: " + proposal.getId());
        proposal.setCreateDate(ZonedDateTime.now());
        proposal.setExpert(expert);
        proposal.setOrder(order);
        proposal.setDuration(dto.estimatedDuration());
        proposal.setProposedPrice(dto.suggestedPrice());
        proposal.setProposedStartAt(dto.suggestedStartTime());
        repository.save(proposal);

        if (repository.countByOrder(order) == 1) {
            order.setStatus(OrderStatus.PROPOSAL_SELECTED);
            orderService.save(order);
        }

    }



    @Override
    public List<ProposalViewDto> getOrderProposals(Long orderId, String sortBy) {
        List<Proposal> proposals = repository.findByOrderId(orderId);

        if ("score".equalsIgnoreCase(sortBy)) {
            proposals = proposals.stream()
                    .filter(p -> p.getExpert() != null && p.getExpert().getScore() != null)
                    .collect(Collectors.toList());
            proposals.sort(Comparator.comparing(
                    (Proposal p) -> p.getExpert().getScore(),
                    Comparator.reverseOrder()
            ));
        } else {
            proposals.sort(Comparator.comparing(Proposal::getProposedPrice));
        }
        return mapper.toViewDtoList(proposals);
    }




}