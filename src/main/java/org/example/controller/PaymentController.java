package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;
import org.example.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/pay-for-order")
    public PaymentResultDto payForOrder(@RequestBody @Valid PaymentRequestDto paymentRequest, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);

        return paymentService.payForOrder(userId, paymentRequest);
    }


    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
