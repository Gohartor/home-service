package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;
import org.example.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


//    @PostMapping("/pay-for-order")
//    public ResponseEntity<PaymentResultDto>  payForOrder(@RequestBody @Valid PaymentRequestDto paymentRequest, Authentication authentication) {
//        Long userId = getUserIdFromAuth(authentication);
//        return ResponseEntity.ok(paymentService.payForOrder(userId, paymentRequest));
//    }


    @PostMapping("/pay-for-order")
    public ResponseEntity<PaymentResultDto> payForOrder(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid PaymentRequestDto paymentRequest)
    {
        return ResponseEntity.ok(paymentService.payForOrder(userId, paymentRequest));
    }



    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
