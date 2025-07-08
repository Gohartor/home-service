package org.example.service;

import org.example.entity.Order;
import org.example.entity.Proposal;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order save(Order entity);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    void updateOrder(Order order);
    long getProposalCountForOrder(Long orderId);
}