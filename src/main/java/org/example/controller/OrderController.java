package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.OrderDetailDto;
import org.example.dto.order.OrderHistoryFilterDto;
import org.example.dto.order.OrderSummaryDto;
import org.example.entity.User;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping("/test")
    public String test() {
        return "hello from backend!";
    }


//    @GetMapping("/history")
//    @PreAuthorize("hasRole('EXPERT')")
//    public ResponseEntity<List<ExpertOrderSummaryDto>> getOrderHistory(@AuthenticationPrincipal User principal) {
//        List<ExpertOrderSummaryDto> dtos =
//                orderService.getExpertOrderHistory(principal.getId());
//        return ResponseEntity.ok(dtos);
//    }

    @GetMapping("/history")
    public ResponseEntity<List<ExpertOrderSummaryDto>> getOrderHistory(
            @RequestParam("expertId") Long expertId
    ) {
        List<ExpertOrderSummaryDto> dtos =
                orderService.getExpertOrderHistory(expertId);
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


    @PostMapping("/select-proposal")
    public ResponseEntity<String> selectProposal(
            @RequestParam Long proposalId,
            @RequestParam Long orderId) {
        orderService.selectProposal(orderId, proposalId);
        return ResponseEntity.ok("success select proposal");
    }


    @PostMapping("/start-order/{orderId}")
    public ResponseEntity<String> startOrder(@PathVariable Long orderId) {
        orderService.startOrder(orderId);
        return ResponseEntity.ok("success start order");
    }

    @PostMapping("/finish-order/{orderId}")
    public ResponseEntity<String> finishOrder(@PathVariable Long orderId) {
        orderService.finishOrder(orderId);
        return ResponseEntity.ok("success finish order");
    }

    @PostMapping("/history-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryDto>> getOrderHistory(@Valid @RequestBody OrderHistoryFilterDto orderHistoryFilterDto) {
        return ResponseEntity.ok(orderService.getOrderSummaryHistoryForAdmin(orderHistoryFilterDto));
    }


    @GetMapping("/detail/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable Long orderId) {
        OrderDetailDto dto = orderService.getOrderDetailHistoryForAdmin(orderId);
        return ResponseEntity.ok(dto);
    }

}
