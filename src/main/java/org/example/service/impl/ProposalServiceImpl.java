package org.example.service.impl;

import org.example.dto.proposal.ProposalRequestDto;
import org.example.dto.proposal.ProposalResponseDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.entity.enumerator.RoleType;
import org.example.entity.enumerator.ServiceStatus;
import org.example.mapper.ProposalMapper;
import org.example.repository.OrderRepository;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.mapstruct.Mapper;
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

    public ProposalServiceImpl(ProposalRepository repository, OrderService orderService, UserService userService, ProposalMapper mapper) {
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


    @Transactional
    @Override
    public ProposalResponseDto createProposal(ProposalRequestDto dto) {
        Order order = orderService.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() != ServiceStatus.PENDING_PROPOSAL &&
                order.getStatus() != ServiceStatus.AWAITING_SPECIALIST) {
            throw new IllegalStateException("Order status not allowed for proposal");
        }

        User expert = userService.findById(dto.getExpertId())
                .orElseThrow(() -> new IllegalArgumentException("Expert not found"));

        if (expert.getRole() != RoleType.EXPERT) {
            throw new IllegalArgumentException("User is not an expert");
        }

        Proposal proposal = mapper.toProposal(dto);
        proposal.setOrder(order);
        proposal.setExpert(expert);

        Proposal saved = repository.save(proposal);

        // اگر اولین پیشنهاد است...
        if (repository.countAllByOrder_Id(dto.getOrderId()) == 1) {
            order.setStatus(ServiceStatus.AWAITING_SPECIALIST);
            orderService.save(order);
        }

        return mapper.toDto(saved);
    }


}