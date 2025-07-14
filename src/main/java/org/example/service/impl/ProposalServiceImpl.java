package org.example.service.impl;

import org.example.dto.proposal.ProposalCreateDto;
import org.example.dto.proposal.ProposalRequestDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.entity.enumerator.RoleType;
import org.example.entity.enumerator.ServiceStatus;
import org.example.mapper.ProposalMapper;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository repository;
    private final OrderService orderService;
    private final UserService userService;
    private final ProposalMapper mapper;

    public ProposalServiceImpl(ProposalRepository repository, OrderService orderService, UserService userService, ProposalMapper mapper, ProposalMapper proposalMapper) {
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
    public void createProposal(Long expertId, ProposalCreateDto dto) {

        if (repository.existsByExpertIdAndOrderId(expertId, dto.orderId())) {
            throw new IllegalStateException("You have already submitted a proposal for this order.");
        }


        Order order = orderService.findById(dto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        User expert = userService.findById(expertId)
                .orElseThrow(() -> new IllegalArgumentException("Expert not found"));

        if (!expert.getServices().contains(order.getService())) {
            throw new IllegalStateException("You are not allowed to submit a proposal for this order.");
        }


        Proposal proposal = mapper.fromDto(dto);
        proposal.setCreateDate(ZonedDateTime.now());
        proposal.setExpert(expert);
        proposal.setOrder(order);
        repository.save(proposal);
    }



}