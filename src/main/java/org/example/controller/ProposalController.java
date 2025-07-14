package org.example.controller;

import jakarta.validation.Valid;

import org.example.auth.UserPrincipal;
import org.example.dto.proposal.ProposalCreateDto;
import org.example.dto.proposal.ProposalRequestDto;
import org.example.dto.proposal.ProposalResponseDto;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.mapper.ProposalMapper;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalService proposalService;
    private final OrderService orderService;
    private final UserService userService;
    private final ProposalMapper proposalMapper;

    public ProposalController(ProposalService proposalService, OrderService orderService, UserService userService, ProposalMapper proposalMapper) {
        this.proposalService = proposalService;
        this.orderService = orderService;
        this.userService = userService;
        this.proposalMapper = proposalMapper;
    }

    @PostMapping("/submit-proposal")
    public ResponseEntity<ProposalResponseDto> submitProposal(@Valid @RequestBody ProposalRequestDto dto) {
        Order order = orderService.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("order not found"));

        User expert = userService.findById(dto.getExpertId())
                .orElseThrow(() -> new IllegalArgumentException("expert not found"));

        Proposal proposal = proposalMapper.toProposal(dto);
        proposal.setOrder(order);
        proposal.setExpert(expert);

        Proposal saved = proposalService.save(proposal);

        return ResponseEntity.ok(proposalMapper.toDto(saved));
    }




        @PostMapping
        public ResponseEntity<String> createProposal(
                @AuthenticationPrincipal UserPrincipal expert,
                @RequestBody @Valid ProposalCreateDto dto) {
            proposalService.createProposal(expert.getId(), dto);
            return ResponseEntity.ok("ok");
        }
    }
