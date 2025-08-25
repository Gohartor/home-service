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
import org.webjars.NotFoundException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
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
    void getExpertOrderHistory_shouldReturnMappedDtos() {
        // Arrange
        Long expertId = 10L;

        Order o1 = new Order();
        Order o2 = new Order();
        List<Order> orders = List.of(o1, o2);

        when(repository.findAllByExpertIdOrderByCreateDateDesc(expertId)).thenReturn(orders);

        ExpertOrderSummaryDto dto1 = mock(ExpertOrderSummaryDto.class);
        ExpertOrderSummaryDto dto2 = mock(ExpertOrderSummaryDto.class);

        when(orderMapper.fromOrderToExpertOrderSummaryDto(o1)).thenReturn(dto1);
        when(orderMapper.fromOrderToExpertOrderSummaryDto(o2)).thenReturn(dto2);

        // Act
        List<ExpertOrderSummaryDto> result = orderService.getExpertOrderHistory(expertId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
        verify(repository).findAllByExpertIdOrderByCreateDateDesc(expertId);
        verify(orderMapper).fromOrderToExpertOrderSummaryDto(o1);
        verify(orderMapper).fromOrderToExpertOrderSummaryDto(o2);
    }

    @Test
    void getExpertOrderHistory_shouldReturnEmptyListWhenNoOrders() {
        // Arrange
        Long expertId = 20L;
        when(repository.findAllByExpertIdOrderByCreateDateDesc(expertId)).thenReturn(List.of());

        // Act
        List<ExpertOrderSummaryDto> result = orderService.getExpertOrderHistory(expertId);

        // Assert
        assertTrue(result.isEmpty());
        verify(repository).findAllByExpertIdOrderByCreateDateDesc(expertId);
        verifyNoInteractions(orderMapper);
    }








    @Test
    void getExpertOrderDetail_shouldReturnDetailDtoWhenExpertMatches() {
        // Arrange
        Long orderId = 1L;
        Long expertId = 2L;

        User expert = new User();
        expert.setId(expertId);

        Order order = new Order();
        order.setExpert(expert);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        ExpertOrderDetailDto expectedDto = mock(ExpertOrderDetailDto.class);
        when(orderMapper.fromOrderToExpertOrderDetailDto(order)).thenReturn(expectedDto);

        // Act
        ExpertOrderDetailDto result = orderService.getExpertOrderDetail(orderId, expertId);

        // Assert
        assertEquals(expectedDto, result);
        verify(repository).findById(orderId);
        verify(orderMapper).fromOrderToExpertOrderDetailDto(order);
    }

    @Test
    void getExpertOrderDetail_shouldThrowWhenOrderNotFound() {
        // Arrange
        Long orderId = 1L;
        Long expertId = 2L;

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NoSuchElementException.class,
                () -> orderService.getExpertOrderDetail(orderId, expertId));
        verify(repository).findById(orderId);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void getExpertOrderDetail_shouldThrowAccessDeniedWhenExpertMismatch() {
        // Arrange
        Long orderId = 1L;
        Long expertId = 2L;

        User anotherExpert = new User();
        anotherExpert.setId(999L);

        Order order = new Order();
        order.setExpert(anotherExpert);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        // Act + Assert
        assertThrows(AccessDeniedException.class,
                () -> orderService.getExpertOrderDetail(orderId, expertId));

        verify(repository).findById(orderId);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void getExpertOrderDetail_shouldThrowAccessDeniedWhenExpertIsNull() {
        // Arrange
        Long orderId = 1L;
        Long expertId = 2L;

        Order order = new Order();
        order.setExpert(null); // no expert assigned yet

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        // Act + Assert
        assertThrows(AccessDeniedException.class,
                () -> orderService.getExpertOrderDetail(orderId, expertId));

        verify(repository).findById(orderId);
        verifyNoInteractions(orderMapper);
    }





    // --- 1. موفقیت‌آمیز ---
    @Test
    void createOrderByCustomer_shouldCreateOrderSuccessfully() {
        // Arrange
        Long customerId = 1L;
        Long serviceId = 2L;
        double basePrice = 100;
        double offeredPrice = 150;
        ZonedDateTime expectedDoneAt = ZonedDateTime.now().plusDays(1);

        User customer = new User();
        customer.setId(customerId);

        Service service = new Service();
        service.setId(serviceId);
        service.setBasePrice(basePrice);

        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(customerId);
        when(dto.serviceId()).thenReturn(serviceId);
        when(dto.offeredPrice()).thenReturn(offeredPrice);
        when(dto.expectedDoneAt()).thenReturn(expectedDoneAt);
        when(dto.description()).thenReturn("desc");
        when(dto.address()).thenReturn("address");

        when(userService.findById(customerId)).thenReturn(Optional.of(customer));
        when(serviceService.findEntityById(serviceId)).thenReturn(Optional.of(service));

        // Act
        orderService.createOrderByCustomer(dto);

        // Assert
        verify(repository).save(argThat(order ->
                order.getCustomer().equals(customer) &&
                        order.getService().equals(service) &&
                        order.getOfferedPrice() == offeredPrice &&
                        order.getExpectedDoneAt().equals(expectedDoneAt) &&
                        order.getStatus() == OrderStatus.PENDING_PROPOSAL &&
                        !order.isPaid() &&
                        order.getTotalPrice() == null
        ));
    }

    // --- 2. مشتری یافت نشود ---
    @Test
    void createOrderByCustomer_shouldThrowWhenCustomerNotFound() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(1L);
        when(userService.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.createOrderByCustomer(dto));

        verify(userService).findById(1L);
        verifyNoInteractions(repository);
    }

    // --- 3. سرویس یافت نشود ---
    @Test
    void createOrderByCustomer_shouldThrowWhenServiceNotFound() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(1L);
        when(dto.serviceId()).thenReturn(2L);

        User customer = new User();
        customer.setId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceService.findEntityById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.createOrderByCustomer(dto));

        verifyNoInteractions(repository);
    }

    // --- 4. قیمت پیشنهادی کمتر از قیمت پایه ---
    @Test
    void createOrderByCustomer_shouldThrowWhenOfferedPriceLessThanBasePrice() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(1L);
        when(dto.serviceId()).thenReturn(2L);
        when(dto.offeredPrice()).thenReturn(50.0);

        User customer = new User();
        customer.setId(1L);
        Service service = new Service();
        service.setId(2L);
        service.setBasePrice(100.0);

        when(userService.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceService.findEntityById(2L)).thenReturn(Optional.of(service));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderByCustomer(dto));

        verifyNoInteractions(repository);
    }

    // --- 5. زمان گذشته ---
    @Test
    void createOrderByCustomer_shouldThrowWhenExpectedDoneAtIsPast() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(1L);
        when(dto.serviceId()).thenReturn(2L);
        when(dto.offeredPrice()).thenReturn(150.0);
        when(dto.expectedDoneAt()).thenReturn(ZonedDateTime.now().minusDays(1));

        User customer = new User();
        customer.setId(1L);
        Service service = new Service();
        service.setId(2L);
        service.setBasePrice(100.0);

        when(userService.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceService.findEntityById(2L)).thenReturn(Optional.of(service));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderByCustomer(dto));

        verifyNoInteractions(repository);
    }

    // --- 6. Service ID نال ---
    @Test
    void createOrderByCustomer_shouldThrowWhenServiceIdIsNull() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(1L);
        when(dto.serviceId()).thenReturn(null);

        User customer = new User();
        customer.setId(1L);
        Service service = new Service();
        service.setBasePrice(100.0);

        when(userService.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceService.findEntityById(null)).thenReturn(Optional.of(service));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderByCustomer(dto));

        verifyNoInteractions(repository);
    }

    // --- 7. Customer ID نال ---
    @Test
    void createOrderByCustomer_shouldThrowWhenCustomerIdIsNull() {
        CreateOrderByCustomerDto dto = mock(CreateOrderByCustomerDto.class);
        when(dto.customerId()).thenReturn(null);
        when(dto.serviceId()).thenReturn(2L);

        User customer = new User();
        Service service = new Service();
        service.setBasePrice(100.0);

        when(userService.findById(null)).thenReturn(Optional.ofNullable(customer));
        when(serviceService.findEntityById(2L)).thenReturn(Optional.of(service));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderByCustomer(dto));

        verifyNoInteractions(repository);
    }









        // --- 1. موفقیت‌آمیز ---
        @Test
        void selectProposal_shouldUpdateOrderAndAcceptProposal_WhenValid() {
            Long orderId = 1L;
            Long proposalId = 2L;

            Order order = new Order();
            order.setId(orderId);

            User expert = new User();
            expert.setId(100L);

            Proposal proposal = new Proposal();
            proposal.setId(proposalId);
            proposal.setProposedPrice(500.0);
            proposal.setExpert(expert);
            proposal.setOrder(order);

            when(repository.findById(orderId)).thenReturn(Optional.of(order));
            when(proposalService.findById(proposalId)).thenReturn(Optional.of(proposal));

            // Act
            orderService.selectProposal(orderId, proposalId);

            // Assert
            assertEquals(500.0, order.getTotalPrice());
            assertEquals(OrderStatus.PROPOSAL_SELECTED, order.getStatus());
            assertEquals(expert, order.getExpert());
            assertTrue(proposal.isAccepted());
            verify(repository).save(order);
        }

        // --- 2. Order not found ---
        @Test
        void selectProposal_shouldThrowWhenOrderNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.selectProposal(1L, 2L));

            verify(repository).findById(1L);
            verifyNoInteractions(proposalService);
            verifyNoMoreInteractions(repository);
        }

        // --- 3. Proposal not found ---
        @Test
        void selectProposal_shouldThrowWhenProposalNotFound() {
            Long orderId = 1L;
            Long proposalId = 2L;
            Order order = new Order();
            order.setId(orderId);

            when(repository.findById(orderId)).thenReturn(Optional.of(order));
            when(proposalService.findById(proposalId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.selectProposal(orderId, proposalId));

            verify(repository).findById(orderId);
            verify(proposalService).findById(proposalId);
            verifyNoMoreInteractions(repository);
        }

        // --- 4. Proposal belongs to another order ---
        @Test
        void selectProposal_shouldThrowWhenProposalDoesNotBelongToOrder() {
            Long orderId = 1L;
            Long proposalId = 2L;
            Order order = new Order();
            order.setId(orderId);

            Order anotherOrder = new Order();
            anotherOrder.setId(999L);

            Proposal proposal = new Proposal();
            proposal.setOrder(anotherOrder);

            when(repository.findById(orderId)).thenReturn(Optional.of(order));
            when(proposalService.findById(proposalId)).thenReturn(Optional.of(proposal));

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.selectProposal(orderId, proposalId));

            verify(repository).findById(orderId);
            verify(proposalService).findById(proposalId);
            verifyNoMoreInteractions(repository);
        }










        // --- 1. موفقیت ---
        @Test
        void expertArrived_shouldMarkOrderAsExpertArrived_WhenValid() {
            Long orderId = 1L;
            Long expertId = 10L;

            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.PROPOSAL_SELECTED);

            User expert = new User();
            expert.setId(expertId);

            Proposal acceptedProposal = new Proposal();
            acceptedProposal.setExpert(expert);

            when(repository.findById(orderId)).thenReturn(Optional.of(order));
            when(proposalService.findByOrderIdAndIsAcceptedTrue(orderId))
                    .thenReturn(Optional.of(acceptedProposal));

            // Act
            orderService.expertArrived(orderId, expertId);

            // Assert
            assertEquals(OrderStatus.EXPERT_ARRIVED, order.getStatus());
            verify(repository).save(order);
        }

        // --- 2. Order not found ---
        @Test
        void expertArrived_shouldThrowWhenOrderNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> orderService.expertArrived(1L, 10L));

            verify(repository).findById(1L);
            verifyNoInteractions(proposalService);
        }

        // --- 3. Status != PROPOSAL_SELECTED ---
        @Test
        void expertArrived_shouldThrowWhenOrderStatusIsInvalid() {
            Order order = new Order();
            order.setStatus(OrderStatus.IN_PROGRESS);

            when(repository.findById(1L)).thenReturn(Optional.of(order));

            assertThrows(BusinessException.class,
                    () -> orderService.expertArrived(1L, 10L));

            verifyNoInteractions(proposalService);
        }

        // --- 4. No accepted proposal ---
        @Test
        void expertArrived_shouldThrowWhenNoAcceptedProposal() {
            Order order = new Order();
            order.setId(1L);
            order.setStatus(OrderStatus.PROPOSAL_SELECTED);

            when(repository.findById(1L)).thenReturn(Optional.of(order));
            when(proposalService.findByOrderIdAndIsAcceptedTrue(1L))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> orderService.expertArrived(1L, 10L));
        }

        // --- 5. Expert mismatch ---
        @Test
        void expertArrived_shouldThrowWhenExpertMismatch() {
            Long orderId = 1L;

            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.PROPOSAL_SELECTED);

            User anotherExpert = new User();
            anotherExpert.setId(99L);

            Proposal acceptedProposal = new Proposal();
            acceptedProposal.setExpert(anotherExpert);

            when(repository.findById(orderId)).thenReturn(Optional.of(order));
            when(proposalService.findByOrderIdAndIsAcceptedTrue(orderId))
                    .thenReturn(Optional.of(acceptedProposal));

            assertThrows(BusinessException.class,
                    () -> orderService.expertArrived(orderId, 10L));

            verify(repository, never()).save(any());
        }








    // 1. موفقیت‌آمیز
    @Test
    void startOrder_shouldSetStatusToInProgress_WhenValid() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.EXPERT_ARRIVED);

        Proposal acceptedProposal = new Proposal();
        acceptedProposal.setProposedStartAt(ZonedDateTime.now().plusHours(1));

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(proposalService.findByOrderIdAndIsAcceptedTrue(orderId))
                .thenReturn(Optional.of(acceptedProposal));

        orderService.startOrder(orderId);

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        verify(repository).save(order);
    }

    // 2. Order not found
    @Test
    void startOrder_shouldThrowWhenOrderNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.startOrder(1L));

        verify(repository).findById(1L);
        verifyNoInteractions(proposalService);
    }

    // 3. Status != EXPERT_ARRIVED
    @Test
    void startOrder_shouldThrowWhenStatusInvalid() {
        Order order = new Order();
        order.setStatus(OrderStatus.PROPOSAL_SELECTED);

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.startOrder(1L));

        verifyNoInteractions(proposalService);
        verify(repository, never()).save(any());
    }

    // 4. No accepted proposal
    @Test
    void startOrder_shouldThrowWhenNoAcceptedProposal() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.EXPERT_ARRIVED);

        when(repository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalService.findByOrderIdAndIsAcceptedTrue(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderService.startOrder(1L));

        verify(repository, never()).save(any());
    }

    // 5. proposedStartAt == null
    @Test
    void startOrder_shouldThrowWhenProposedStartAtNull() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.EXPERT_ARRIVED);

        Proposal acceptedProposal = new Proposal();
        acceptedProposal.setProposedStartAt(null);

        when(repository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalService.findByOrderIdAndIsAcceptedTrue(1L))
                .thenReturn(Optional.of(acceptedProposal));

        assertThrows(BusinessException.class, () -> orderService.startOrder(1L));

        verify(repository, never()).save(any());
    }








    // 1) موفقیت‌آمیز
    @Test
    void finishOrder_shouldCompleteOrder_WhenInProgress() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.IN_PROGRESS);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.finishOrder(orderId);

        // Assert
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertNotNull(order.getDoneAt(), "doneAt should be set");
        verify(repository).save(order);
    }

    // 2) Order not found
    @Test
    void finishOrder_shouldThrowWhenOrderNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.finishOrder(1L));

        verifyNoMoreInteractions(repository);
    }

    // 3) Status != IN_PROGRESS
    @Test
    void finishOrder_shouldThrowWhenStatusInvalid() {
        Order order = new Order();
        order.setStatus(OrderStatus.EXPERT_ARRIVED);

        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.finishOrder(1L));

        verify(repository, never()).save(any());
    }









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