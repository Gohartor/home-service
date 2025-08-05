package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.payment.PayRequest;
import org.example.dto.payment.PaymentInitResponse;
import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;
import org.example.entity.PaymentSession;
import org.example.security.CustomUserDetails;
import org.example.service.PaymentService;
import org.example.service.PaymentSessionService;
import org.example.utility.CaptchaUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentSessionService paymentSessionService;
    private final CaptchaUtil captchaUtil;


//    @PostMapping("/pay-for-order")
//    public ResponseEntity<PaymentResultDto>  payForOrder(@RequestBody @Valid PaymentRequestDto paymentRequest, Authentication authentication) {
//        Long userId = getUserIdFromAuth(authentication);
//        return ResponseEntity.ok(paymentService.payForOrder(userId, paymentRequest));
//    }


    @PostMapping("/pay-for-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentResultDto> payForOrder(
            Authentication authentication,
            @RequestParam Long orderId)
    {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();
        return ResponseEntity.ok(paymentService.payForOrder(currentUserId, orderId));
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




    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<?> pay(@RequestBody PayRequest dto) {
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
