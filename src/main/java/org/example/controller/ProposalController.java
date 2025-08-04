package org.example.controller;

import jakarta.validation.Valid;

import org.example.dto.proposal.ProposalCreateByExpertDto;
import org.example.dto.proposal.ProposalViewDto;
import org.example.mapper.ProposalMapper;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<String> submitProposal(
            @RequestParam Long expertId,
            @RequestBody @Valid ProposalCreateByExpertDto dto) {
        proposalService.submitProposalByExpert(expertId, dto);
        return ResponseEntity.ok("success submit proposal");
    }



    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<ProposalViewDto>> getOrderProposals(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "price") String sortBy
    ) {
        return ResponseEntity.ok(proposalService.getOrderProposals(orderId, sortBy));
    }


}