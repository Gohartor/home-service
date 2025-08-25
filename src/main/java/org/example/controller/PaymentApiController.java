package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentUrlResponse;
import org.example.dto.payment.PayRequest;
import org.example.entity.PaymentSession;
import org.example.service.PaymentSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentSessionService paymentSessionService;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    public record InitialPayRequest(Long orderId) {}

    @PostMapping("/initiate")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentUrlResponse> initiatePayment(@RequestBody InitialPayRequest initialPayRequest) {
        Long orderId = initialPayRequest.orderId();

        String captcha = "12345";
        PaymentSession session = paymentSessionService.createSession(orderId, captcha);

        String paymentPageUrl = appBaseUrl + "/payment/page?sessionToken=" + session.getToken();

        return ResponseEntity.ok(new PaymentUrlResponse(paymentPageUrl, session.getToken()));
    }


    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody @Valid PayRequest payRequest) {
        Optional<PaymentSession> optSession = paymentSessionService.findByToken(payRequest.token());

        if (optSession.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid payment session token.");
        }

        PaymentSession session = optSession.get();

        if (session.getExpireAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Payment session has expired.");
        }


        if (session.getPaid()) {
            return ResponseEntity.badRequest().body("This payment has already been processed.");
        }

        paymentSessionService.markPaid(session);

        return ResponseEntity.ok("Payment processed successfully for Order ID: " + session.getOrderId());
    }
}
