package org.example.service.impl;

import org.example.dto.PageCustom;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    void submitProposalByExpert_shouldDenyIfPriceLowerThanOffered() {
        Long expertId = 1L;
        long orderId = 2L;

        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(orderId);
        when(dto.suggestedPrice()).thenReturn(90.0);

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_PROPOSAL);
        order.setOfferedPrice(100.0);
        order.setExpectedDoneAt(ZonedDateTime.now().plusDays(1));

        when(repository.existsByExpertIdAndOrderId(expertId, orderId)).thenReturn(false);
        when(orderService.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class,
                () -> proposalService.submitProposalByExpert(expertId, dto));

        verify(repository, never()).save(any());
    }

    @Test
    void submitProposalByExpert_shouldDenyIfStartTimeNotAfterExpected() {
        Long expertId = 1L;
        long orderId = 2L;

        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(orderId);
        when(dto.suggestedPrice()).thenReturn(150.0);
        when(dto.suggestedStartTime()).thenReturn(ZonedDateTime.now().plusDays(1));

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_PROPOSAL);
        order.setOfferedPrice(100.0);
        order.setExpectedDoneAt(dto.suggestedStartTime()); // مساوی → شرط fail

        when(repository.existsByExpertIdAndOrderId(expertId, orderId)).thenReturn(false);
        when(orderService.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class,
                () -> proposalService.submitProposalByExpert(expertId, dto));

        verify(repository, never()).save(any());
    }

    @Test
    void submitProposalByExpert_shouldSaveProposal_WhenValid() {
        Long expertId = 1L;
        long orderId = 2L;

        ProposalCreateByExpertDto dto = mock(ProposalCreateByExpertDto.class);
        when(dto.orderId()).thenReturn(orderId);
        when(dto.suggestedPrice()).thenReturn(150.0);
        when(dto.suggestedStartTime()).thenReturn(ZonedDateTime.now().plusDays(3));
        when(dto.estimatedDuration()).thenReturn(5);

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_PROPOSAL);
        order.setOfferedPrice(100.0);
        order.setExpectedDoneAt(ZonedDateTime.now().plusDays(1));

        Service neededService = new Service();
        order.setService(neededService);

        User expert = new User();
        expert.setServices(Set.of(neededService));

        Proposal mappedProposal = new Proposal();
        when(mapper.fromDto(dto)).thenReturn(mappedProposal);

        when(repository.existsByExpertIdAndOrderId(expertId, orderId)).thenReturn(false);
        when(orderService.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.findById(expertId)).thenReturn(Optional.of(expert));

        proposalService.submitProposalByExpert(expertId, dto);

        assertEquals(order, mappedProposal.getOrder());
        assertEquals(expert, mappedProposal.getExpert());
        assertEquals(150.0, mappedProposal.getProposedPrice());
        assertEquals(dto.suggestedStartTime(), mappedProposal.getProposedStartAt());
        assertEquals(5, mappedProposal.getDuration());
        assertNotNull(mappedProposal.getCreateDate());

        verify(repository).save(mappedProposal);
    }







    @Test
    void getOrderProposals_shouldUsePriceSorting_WhenSortByIsPrice() {
        Long orderId = 10L;

        List<Proposal> proposals = List.of(new Proposal());
        List<ProposalViewDto> dtos = List.of(mock(ProposalViewDto.class));

        when(repository.findByOrderIdOrderByProposedPriceDesc(orderId))
                .thenReturn(proposals);
        when(mapper.toViewDtoList(proposals)).thenReturn(dtos);

        List<ProposalViewDto> result = proposalService.getOrderProposals(orderId, "price");

        assertSame(dtos, result);
        verify(repository).findByOrderIdOrderByProposedPriceDesc(orderId);
        verify(mapper).toViewDtoList(proposals);
        verifyNoMoreInteractions(repository);
    }





    @Test
    void getOrderProposalsPage_shouldReturnPageFromRepository() {
        Long orderId = 5L;
        PageCustom pageCustom = new PageCustom(1, 20, "proposedPrice");
        Page<Proposal> expectedPage = new PageImpl<>(List.of(new Proposal()));

        when(repository.getProposalsByOrder_Id(eq(orderId), any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<Proposal> result = proposalService.getOrderProposalsPage(orderId, pageCustom);

        assertSame(expectedPage, result);

        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).getProposalsByOrder_Id(eq(orderId), captor.capture());

        PageRequest pr = captor.getValue();
        assertEquals(1, pr.getPageNumber());
        assertEquals(20, pr.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "proposedPrice"), pr.getSort());
    }

    @Test
    void findByOrderIdAndIsAcceptedTrue_shouldReturnValue_WhenPresent() {
        Long orderId = 99L;
        Proposal proposal = new Proposal();

        when(repository.findByOrderIdAndIsAcceptedTrue(orderId))
                .thenReturn(Optional.of(proposal));

        Optional<Proposal> result = proposalService.findByOrderIdAndIsAcceptedTrue(orderId);

        assertTrue(result.isPresent());
        assertSame(proposal, result.get());
    }

    @Test
    void findByOrderIdAndIsAcceptedTrue_shouldReturnEmpty_WhenNotPresent() {
        Long orderId = 100L;

        when(repository.findByOrderIdAndIsAcceptedTrue(orderId))
                .thenReturn(Optional.empty());

        Optional<Proposal> result = proposalService.findByOrderIdAndIsAcceptedTrue(orderId);

        assertTrue(result.isEmpty());
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
    void findByOrderIdAndIsAcceptedTrue_shouldReturnOptional() {
        Proposal proposal = new Proposal();
        when(repository.findByOrderIdAndIsAcceptedTrue(135L)).thenReturn(Optional.of(proposal));

        Optional<Proposal> result = proposalService.findByOrderIdAndIsAcceptedTrue(135L);

        assertTrue(result.isPresent());
        assertSame(proposal, result.get());
    }

}