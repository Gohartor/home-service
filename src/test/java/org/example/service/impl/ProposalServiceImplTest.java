package org.example.service.impl;

import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.dto.proposal.ProposalViewDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.OrderStatus;
import org.example.mapper.ProposalMapper;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    @Mock
    ProposalRepository repository;
    @Mock
    OrderService orderService;
    @Mock
    UserService userService;
    @Mock
    ProposalMapper mapper;

    @InjectMocks
    ProposalServiceImpl proposalService;

    @Test
    void countAllByOrder_Id_shouldReturnCount() {
        when(repository.countAllByOrder_Id(111L)).thenReturn(3L);
        long count = proposalService.countAllByOrder_Id(111L);
        assertEquals(3L, count);
        verify(repository).countAllByOrder_Id(111L);
    }




    @Test
    void submitProposalByExpert_shouldThrowIfAlreadySubmitted() {
        Long expertId = 1L;
        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(2L);
        when(repository.existsByExpertIdAndOrderId(expertId, 2L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> proposalService.submitProposalByExpert(expertId, dto));
    }

    @Test
    void submitProposalByExpert_shouldThrowIfOrderNotFound() {
        Long expertId = 1L;
        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(2L);
        when(repository.existsByExpertIdAndOrderId(anyLong(), anyLong())).thenReturn(false);
        when(orderService.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> proposalService.submitProposalByExpert(expertId, dto));
    }


    @Test
    void submitProposalByExpert_shouldThrowIfOrderNotOpen() {
        Long expertId = 1L;
        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(2L);
        when(repository.existsByExpertIdAndOrderId(anyLong(), anyLong())).thenReturn(false);

        Order order = new Order(); order.setStatus(OrderStatus.IN_PROGRESS);
        when(orderService.findById(2L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> proposalService.submitProposalByExpert(expertId, dto));
    }

    @Test
    void submitProposalByExpert_shouldDenyIfExpertNotAllowed() {
        Long expertId = 1L;
        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(2L);
        when(repository.existsByExpertIdAndOrderId(anyLong(), anyLong())).thenReturn(false);

        Order order = new Order();
        Service neededService = new Service();
        order.setService(neededService);
        order.setStatus(OrderStatus.PENDING_PROPOSAL);
        when(orderService.findById(2L)).thenReturn(Optional.of(order));

        User expert = new User();
        expert.setServices(Set.of());
        when(userService.findById(expertId)).thenReturn(Optional.of(expert));

        assertThrows(AccessDeniedException.class, () -> proposalService.submitProposalByExpert(expertId, dto));
    }

    @Test
    void getOrderProposals_shouldSortByProposedPriceByDefault() {
        Proposal p1 = new Proposal(); p1.setProposedPrice(50000D);
        Proposal p2 = new Proposal(); p2.setProposedPrice(15000D);

        when(repository.findByOrderId(100L)).thenReturn(new ArrayList<>(List.of(p1, p2)));
        List<ProposalViewDto> expectedDtos = List.of(mock(ProposalViewDto.class), mock(ProposalViewDto.class));
        when(mapper.toViewDtoList(anyList())).thenReturn(expectedDtos);

        List<ProposalViewDto> result = proposalService.getOrderProposals(100L, null);

        assertEquals(expectedDtos, result);
    }


    @Test
    void getOrderProposals_shouldSortByScoreIfRequested() {
        Proposal p1 = new Proposal();
        User expert1 = new User(); expert1.setScore(4.5);
        p1.setExpert(expert1);

        Proposal p2 = new Proposal();
        User expert2 = new User(); expert2.setScore(2.3);
        p2.setExpert(expert2);

        Proposal p3 = new Proposal();
        p3.setExpert(new User());

        when(repository.findByOrderId(200L)).thenReturn(List.of(p1, p2, p3));
        List<ProposalViewDto> expectedDtos = List.of(mock(ProposalViewDto.class));
        when(mapper.toViewDtoList(anyList())).thenReturn(expectedDtos);

        List<ProposalViewDto> result = proposalService.getOrderProposals(200L, "score");

        assertEquals(expectedDtos, result);
    }


    @Test
    void findByOrderIdAndIsAcceptedTrue_shouldReturnOptional() {
        Proposal proposal = new Proposal();
        when(repository.findByOrderIdAndIsAcceptedTrue(135L)).thenReturn(Optional.of(proposal));

        Optional<Proposal> result = proposalService.findByOrderIdAndIsAcceptedTrue(135L);

        assertTrue(result.isPresent());
        assertSame(proposal, result.get());
    }

}