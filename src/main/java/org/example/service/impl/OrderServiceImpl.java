package org.example.service.impl;

import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.OrderMapper;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.enumerator.ServiceStatus;
import org.example.repository.OrderRepository;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final ProposalService proposalService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository repository, @Lazy ProposalService proposalService, OrderMapper orderMapper) {
        this.repository = repository;
        this.proposalService = proposalService;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Order> findAll() {
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


    public void updateOrder(Order order) {
        repository.save(order);
    }

    public long getProposalCountForOrder(Long orderId) {
        return proposalService.countAllByOrder_Id(orderId);
    }

    public boolean hasActiveOrderForExpert(Long expertId) {
        return repository.existsByExpert_IdAndStatus(expertId, ServiceStatus.AWAITING_SPECIALIST);
    }


    public List<ExpertOrderSummaryDto> getExpertOrderHistory(Long expertId) {
        List<Order> orders = repository.findAllByExpertIdOrderByCreateDateDesc(expertId);
        return orders.stream()
                .map(orderMapper::toSummaryDto)
                .toList();
    }


    public ExpertOrderDetailDto getExpertOrderDetail(Long orderId, Long expertId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        if (order.getExpert() == null || !order.getExpert().getId().equals(expertId))
            throw new AccessDeniedException("You are not allowed to view this order");
        return orderMapper.toDetailDto(order);
    }
}