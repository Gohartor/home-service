package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.dto.order.OrderMapper;
import org.example.entity.Order;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.ServiceStatus;
import org.example.repository.OrderRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.ServiceService;
import org.example.service.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final ServiceService serviceService;
    private final UserService userService;
    private final ProposalService proposalService;
    private final OrderMapper orderMapper;


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



    public void createOrderByCustomer(CreateOrderByCustomerDto dto) {

        User customer = userService.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Service service = serviceService.findEntityById(dto.serviceId())
                .orElseThrow(() -> new NotFoundException("Service not found"));

        if (dto.offeredPrice() < service.getBasePrice()) {
            throw new IllegalArgumentException("Offered price must be at least the base price of this service.");
        }

        Order order = new Order();

        if (dto.serviceId() == null) {
            throw new IllegalArgumentException("Service ID must not be null");
        }
        order.setService(service);

        if (dto.customerId() == null) {
            throw new IllegalArgumentException("Customer ID must not be null");
        }
        order.setCustomer(customer);

        order.setDescription(dto.description());
        order.setOfferedPrice((double) dto.offeredPrice());
        order.setAddress(dto.address());
        order.setExpectedDoneAt(dto.expectedDoneAt());
        order.setStatus(ServiceStatus.PENDING_PROPOSAL);
        order.setPaid(false);
        order.setTotalPrice(null);


        repository.save(order);
    }
}