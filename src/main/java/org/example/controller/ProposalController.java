package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.proposal.ProposalResponseDTO;
import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.mapper.ProposalMapper;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalService proposalService;
    private final OrderService orderService;
    private final UserService userService;

    public ProposalController(ProposalService proposalService, OrderService orderService, UserService userService) {
        this.proposalService = proposalService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ProposalResponseDTO> submitProposal(@Valid @RequestBody ProposalResponseDTO dto) {
        Order order = orderService.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        User expert = userService.findById(dto.getExpertId())
                .orElseThrow(() -> new IllegalArgumentException("Expert not found"));

        Proposal proposal = ProposalMapper.toEntity(dto, order, expert);
        Proposal saved = proposalService.save(proposal);

        // TODO: اگر اولین پیشنهاد باشد، وضعیت سفارش را به "منتظر انتخاب متخصص" تغییر بده

        return ResponseEntity.ok(ProposalMapper.toDto(saved));
    }
}
