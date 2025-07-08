package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.repository.OrderRepository;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final ProposalService proposalService;

    public OrderServiceImpl(OrderRepository repository, @Lazy ProposalService proposalService) {
        this.repository = repository;
        this.proposalService = proposalService;
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
}