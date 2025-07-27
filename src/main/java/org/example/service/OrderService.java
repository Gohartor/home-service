package org.example.service;

import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;

import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.dto.order.OrderDetailDto;
import org.example.dto.order.OrderHistoryFilterDto;
import org.example.dto.order.OrderSummaryDto;
import org.example.entity.Order;

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

    boolean hasActiveOrderForExpert(Long expertId);

    List<ExpertOrderSummaryDto> getExpertOrderHistory(Long expertId);
    ExpertOrderDetailDto getExpertOrderDetail(Long orderId, Long expertId);

    void createOrderByCustomer(CreateOrderByCustomerDto dto);

    void selectProposal(Long orderId, Long proposalId);

    void startOrder(Long orderId);

    void finishOrder(Long orderId);

    List<OrderSummaryDto> getOrderSummaryHistoryForAdmin(OrderHistoryFilterDto filter);

    OrderDetailDto getOrderDetailHistoryForAdmin(Long orderId);



}