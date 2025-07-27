package org.example.service.impl;

import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.OrderStatus;
import org.example.exception.BusinessException;
import org.example.mapper.OrderMapper;
import org.example.repository.OrderRepository;
import org.example.service.ProposalService;
import org.example.service.ServiceService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository repository;
    @Mock
    ServiceService serviceService;
    @Mock
    UserService userService;
    @Mock
    ProposalService proposalService;
    @Mock
    OrderMapper orderMapper;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void getProposalCountForOrder_shouldReturnCount() {
        when(proposalService.countAllByOrder_Id(1L)).thenReturn(5L);
        long result = orderService.getProposalCountForOrder(1L);
        assertEquals(5L, result);
        verify(proposalService).countAllByOrder_Id(1L);
    }


    @Test
    void hasActiveOrderForExpert_shouldReturnTrueIfHasOrder() {
        when(repository.existsByExpert_IdAndStatus(2L, OrderStatus.IN_PROGRESS)).thenReturn(true);
        boolean result = orderService.hasActiveOrderForExpert(2L);
        assertTrue(result);
    }



    @Test
    void getExpertOrderHistory_shouldReturnSummaryDtos() {
        User expert = new User();
        expert.setId(3L);

        Order o1 = new Order(); o1.setExpert(expert);
        Order o2 = new Order(); o2.setExpert(expert);
        List<Order> orders = List.of(o1, o2);

        when(repository.findAllByExpertIdOrderByCreateDateDesc(3L)).thenReturn(orders);

        ExpertOrderSummaryDto dto1 = mock(ExpertOrderSummaryDto.class), dto2 = mock(ExpertOrderSummaryDto.class);
        when(orderMapper.fromOrderToExpertOrderSummaryDto(o1)).thenReturn(dto1);
        when(orderMapper.fromOrderToExpertOrderSummaryDto(o2)).thenReturn(dto2);

        List<ExpertOrderSummaryDto> result = orderService.getExpertOrderHistory(3L);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
    }


    @Test
    void getExpertOrderDetail_shouldReturnDetailDto() {
        Order order = new Order();
        User expert = new User(); expert.setId(4L);
        order.setExpert(expert); order.setId(66L);

        when(repository.findById(66L)).thenReturn(Optional.of(order));
        ExpertOrderDetailDto dto = mock(ExpertOrderDetailDto.class);
        when(orderMapper.fromOrderToExpertOrderDetailDto(order)).thenReturn(dto);

        ExpertOrderDetailDto result = orderService.getExpertOrderDetail(66L, 4L);
        assertEquals(dto, result);
    }

    @Test
    void getExpertOrderDetail_shouldThrowAccessDeniedIfNotExpert() {
        Order order = new Order();
        User expert = new User(); expert.setId(10L);
        order.setExpert(expert); order.setId(66L);

        when(repository.findById(66L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> orderService.getExpertOrderDetail(66L, 20L));
    }


    @Test
    void createOrderByCustomer_shouldThrowIfOfferedPriceLessThanBasePrice() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(11L);
        when(dto.serviceId()).thenReturn(22L);
        when(dto.offeredPrice()).thenReturn(50.0);

        User user = new User(); Service service = new Service();
        service.setBasePrice(100.0);

        when(userService.findById(11L)).thenReturn(Optional.of(user));
        when(serviceService.findEntityById(22L)).thenReturn(Optional.of(service));

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrderByCustomer(dto));
    }


    @Test
    void selectProposal_shouldThrowIfProposalNotBelongToOrder() {
        Order order = new Order(); order.setId(100L);

        Proposal proposal = new Proposal();
        Order otherOrder = new Order(); otherOrder.setId(999L);
        proposal.setOrder(otherOrder);

        when(repository.findById(100L)).thenReturn(Optional.of(order));
        when(proposalService.findById(200L)).thenReturn(Optional.of(proposal));

        assertThrows(IllegalArgumentException.class, () -> orderService.selectProposal(100L, 200L));
    }


    @Test
    void startOrder_shouldThrowIfOrderNotExpertArrived() {
        Order order = new Order(); order.setStatus(OrderStatus.PENDING_PROPOSAL);

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.startOrder(1L));
    }


    @Test
    void finishOrder_shouldThrowIfStatusNotInProgress() {
        Order order = new Order(); order.setStatus(OrderStatus.PENDING_PROPOSAL);

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.finishOrder(1L));
    }

}