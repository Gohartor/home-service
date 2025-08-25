package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ApiResponse;
import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.CreateOrderByCustomerDto;
import org.example.dto.order.OrderDetailDto;
import org.example.dto.order.OrderHistoryFilterDto;
import org.example.dto.order.OrderSummaryDto;
import org.example.entity.User;
import org.example.security.CustomUserDetails;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
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

    //TODO change this to orderController -----> DONE
    @PostMapping("/create-order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createOrderByCustomer(@RequestBody CreateOrderByCustomerDto dto) {
        orderService.createOrderByCustomer(dto);
        return ResponseEntity.ok("Order created successfully.");
    }

    @GetMapping("/history")
    public ResponseEntity<List<ExpertOrderSummaryDto>> getOrderHistory(
            @RequestParam("expertId") Long expertId
    ) {
        List<ExpertOrderSummaryDto> dtos =
                orderService.getExpertOrderHistory(expertId);
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{orderId:\\d+}")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<ExpertOrderDetailDto> getOrderDetail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long orderId) {
        ExpertOrderDetailDto dto =
                orderService.getExpertOrderDetail(orderId, principal.getId());
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/select-proposal")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> selectProposal(
            @RequestParam Long proposalId,
            @RequestParam Long orderId) {
        orderService.selectProposal(orderId, proposalId);
        return ResponseEntity.ok(new ApiResponse("proposal selected successfully"));
    }


    @PutMapping("/expert-arrived")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<ApiResponse> expertArrived(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam Long orderId) {
        Long expertId = principal.getId();
        orderService.expertArrived(orderId, expertId);
        return ResponseEntity.ok(new ApiResponse("expert arrived successfully"));
    }


    @PostMapping("/start-order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    //TODO  @AuthenticationPrincipal CustomUserDetails principal
    public ResponseEntity<ApiResponse> startOrder(@PathVariable Long orderId) {
        orderService.startOrder(orderId);
        return ResponseEntity.ok(new ApiResponse("start order successfully"));
    }

    @PostMapping("/finish-order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> finishOrder(@PathVariable Long orderId) {
        orderService.finishOrder(orderId);
        return ResponseEntity.ok(new ApiResponse("finish order successfully"));
    }

    @PostMapping("/history-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryDto>> getOrderHistory(@Valid @RequestBody OrderHistoryFilterDto orderHistoryFilterDto) {
        return ResponseEntity.ok(orderService.getOrderSummaryHistoryForAdmin(orderHistoryFilterDto));
    }


    @GetMapping("/detail-for-admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable Long orderId) {
        OrderDetailDto dto = orderService.getOrderDetailHistoryForAdmin(orderId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/history-for-customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderSummaryDto>> getOrderHistoryForCustomer(@Valid @RequestBody OrderHistoryFilterDto filter) {
        return ResponseEntity.ok(orderService.getFilteredOrders(filter));
    }


    @GetMapping("/detail-for-customer/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderDetailDto getOrderDetailForCustomer(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }
}
