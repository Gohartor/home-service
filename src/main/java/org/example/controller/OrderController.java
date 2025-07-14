package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.entity.User;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/expert/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping("/history")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<List<ExpertOrderSummaryDto>> getOrderHistory(@AuthenticationPrincipal User principal) {
        List<ExpertOrderSummaryDto> dtos =
                orderService.getExpertOrderHistory(principal.getId());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<ExpertOrderDetailDto> getOrderDetail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long orderId) {
        ExpertOrderDetailDto dto =
                orderService.getExpertOrderDetail(orderId, principal.getId());
        return ResponseEntity.ok(dto);
    }
}
