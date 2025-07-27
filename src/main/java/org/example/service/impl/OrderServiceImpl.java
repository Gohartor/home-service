package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.dto.order.OrderDetailDto;
import org.example.dto.order.OrderHistoryFilterDto;
import org.example.dto.order.OrderSummaryDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.OrderStatus;
import org.example.exception.BusinessException;
import org.example.exception.OrderNotFoundException;
import org.example.mapper.OrderMapper;
import org.example.repository.OrderRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.ServiceService;
import org.example.service.UserService;

import org.example.specification.OrderSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.ZonedDateTime;
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
        return repository.existsByExpert_IdAndStatus(expertId, OrderStatus.IN_PROGRESS);
    }


    public List<ExpertOrderSummaryDto> getExpertOrderHistory(Long expertId) {
        List<Order> orders = repository.findAllByExpertIdOrderByCreateDateDesc(expertId);
        return orders.stream()
                .map(orderMapper::fromOrderToExpertOrderSummaryDto)
                .toList();
    }


    public ExpertOrderDetailDto getExpertOrderDetail(Long orderId, Long expertId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        if (order.getExpert() == null || !order.getExpert().getId().equals(expertId))
            throw new AccessDeniedException("You are not allowed to view this order");
        return orderMapper.fromOrderToExpertOrderDetailDto(order);
    }



    //TODO time condition for future
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
        order.setStatus(OrderStatus.PENDING_PROPOSAL);
        order.setPaid(false);
        order.setTotalPrice(null);


        repository.save(order);
    }



    @Override
    @Transactional
    public void selectProposal(Long orderId, Long proposalId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Proposal proposal = proposalService.findById(proposalId)
                .orElseThrow(() -> new NotFoundException("Proposal not found"));

        if (!proposal.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Proposal does not belong to the order");
        }

        order.setStatus(OrderStatus.PROPOSAL_SELECTED);

        repository.save(order);
    }


    @Override
    @Transactional
    public void startOrder(Long orderId) {

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.EXPERT_ARRIVED) {
            throw new BusinessException("Order is not ready to be started by customer.");
        }

        Proposal proposal = proposalService.findByOrderIdAndIsAcceptedTrue(orderId)
                .orElseThrow(() -> new BusinessException("No accepted proposal for this order."));

        if (proposal.getProposedStartAt() == null) {
            throw new BusinessException("No proposed start time set for the accepted proposal!");
        }

        if (ZonedDateTime.now().isBefore(proposal.getProposedStartAt())) {
            throw new BusinessException("Cannot start the order before the proposed start time.");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        repository.save(order);
    }


    @Override
    @Transactional
    public void finishOrder(Long orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new BusinessException("The order has not started or is already completed/canceled.");
        }

        order.setStatus(OrderStatus.COMPLETED);
        repository.save(order);
    }


//    @Override
//    public List<OrderSummaryDto> getOrderSummaryHistoryForAdmin(OrderHistoryFilterDto filter) {
//        Specification<Order> spec = OrderSpecification.filter(filter);
//        List<Order> orders = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return orders.stream().map(orderMapper::fromOrderToAdminOrderSummaryDto).toList();
//    }

    @Override
    public List<OrderSummaryDto> getOrderSummaryHistoryForAdmin(OrderHistoryFilterDto filter) {
        Specification<Order> spec = OrderSpecification.filter(filter);
        List<Order> orders = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createDate"));
        return orderMapper.fromOrderListToAdminOrderSummaryDtoList(orders);
    }



    @Override
    public OrderDetailDto getOrderDetailHistoryForAdmin(Long orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        return orderMapper.fromOrderToAdminOrderDetailDto(order);
    }
}