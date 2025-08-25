package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentUrlResponse;
import org.example.dto.payment.PayRequest;
import org.example.dto.payment.PaymentInitResponse;
import org.example.dto.payment.PaymentRequestDto; // If you still use this DTO for commented out method
import org.example.dto.payment.PaymentResultDto;
import org.example.entity.PaymentSession;
import org.example.security.CustomUserDetails; // If still needed for other methods not shown
import org.example.service.PaymentService;
import org.example.service.PaymentSessionService;
import org.example.utility.CaptchaUtil; // Assuming CaptchaUtil is a utility, not a Spring Bean here if not autowired
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // If still needed for other methods not shown
import org.springframework.security.core.annotation.AuthenticationPrincipal; // If still needed for other methods not shown
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSessionService paymentSessionService;

    @Value("http://localhost:8080")
    private String appBaseUrl;

    public record InitialPayRequest(Long orderId) {}

    @PostMapping("/pay-for-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentResultDto> payForOrder(
            @RequestParam Long orderId)
    {
        return ResponseEntity.ok(paymentService.payForOrder(orderId));
    }



    @GetMapping("/init/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentInitResponse> initPayment(@PathVariable Long orderId) {
        String captcha = CaptchaUtil.generateText();
        BufferedImage bufferedImage = CaptchaUtil.generateImage(captcha);
        PaymentSession session = paymentSessionService.createSession(orderId, captcha);

        String base64Image = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(new PaymentInitResponse(
                session.getToken(), base64Image
        ));
    }


    @PostMapping("/init")
    public ResponseEntity<PaymentUrlResponse> initiatePayment(@RequestBody InitialPayRequest initialPayRequest) {
        Long orderId = initialPayRequest.orderId();

        String paymentSessionToken = UUID.randomUUID().toString();
        PaymentSession session = paymentSessionService.createSession(orderId, paymentSessionToken);


        String paymentPageUrl = appBaseUrl + "/payment/page?sessionToken=" + session.getToken();

        return ResponseEntity.ok(new PaymentUrlResponse(paymentPageUrl, session.getToken()));
    }



    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody @Valid PayRequest payRequest) {
        Optional<PaymentSession> optSession = paymentSessionService.findByToken(payRequest.token());
        if (optSession.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid payment session token.");
        }

        PaymentSession session = optSession.get();

        if (session.getExpireAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Payment session has expired.");
        }

        if (!session.getCaptchaCode().equalsIgnoreCase(payRequest.captcha())) {
            return ResponseEntity.badRequest().body("Incorrect captcha code.");
        }

        if (session.getPaid()) {
            return ResponseEntity.badRequest().body("This payment has already been processed.");
        }

        paymentSessionService.markPaid(session);

        return ResponseEntity.ok("Payment processed successfully for Order ID: " + session.getOrderId());
    }




//    @PostMapping("/pay")
//    public ResponseEntity<?> pay(@RequestBody @Valid PayRequest dto) {
//        Optional<PaymentSession> optSession = paymentSessionService.findByToken(dto.token());
//        if (optSession.isEmpty()) return ResponseEntity.badRequest().body("invalid token");
//
//        PaymentSession session = optSession.get();
//        if (session.getExpireAt().isBefore(LocalDateTime.now()))
//            return ResponseEntity.badRequest().body("time for pay is expired");
//
//        if (!session.getCaptchaCode().equalsIgnoreCase(dto.captcha()))
//            return ResponseEntity.badRequest().body("captcha is wrong");
//
//        if (session.getPaid())
//            return ResponseEntity.badRequest().body("pay is paid");
//
//        paymentSessionService.markPaid(session);
//
//        return ResponseEntity.ok("success paid");
//    }



    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody @Valid PayRequest dto) {
        Optional<PaymentSession> optSession = paymentSessionService.findByToken(dto.token());
        if (optSession.isEmpty()) return ResponseEntity.badRequest().body("invalid token");

        PaymentSession session = optSession.get();

        if (session.getExpireAt().isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body("time for pay is expired");

        if (!session.getCaptchaCode().equalsIgnoreCase(dto.captcha()))
            return ResponseEntity.badRequest().body("captcha is wrong");

        if (session.getPaid())
            return ResponseEntity.badRequest().body("pay is paid");

        paymentSessionService.markPaid(session);

        return ResponseEntity.ok("success paid");
    }


}
