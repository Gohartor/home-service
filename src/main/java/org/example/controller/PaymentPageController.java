package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.entity.PaymentSession;
import org.example.service.PaymentSessionService;
import org.example.utility.CaptchaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentPageController {

    private final PaymentSessionService paymentSessionService;


//    @GetMapping("/page")
//    public String showPaymentPage(@RequestParam("sessionToken") String sessionToken, Model model) {
//
//        Optional<PaymentSession> optSession = paymentSessionService.findByToken(sessionToken);
//
//        if (optSession.isEmpty()) {
//            model.addAttribute("errorMessage", "Invalid or expired payment session.");
//            return "errorPage";
//        }
//
//        PaymentSession session = optSession.get();
//
//        String captchaBase64Image = generateCaptchaImageBase64(session.getCaptchaCode());
//
//
//        model.addAttribute("token", session.getToken());
//        model.addAttribute("captchaImage", captchaBase64Image);
//        model.addAttribute("expiresAt", session.getExpireAt().toString());
//        model.addAttribute("orderId", session.getOrderId());
//
//
//        return "payment";
//    }


    @GetMapping("/payment/page-other/{orderId}")
    public String showPaymentPage(@PathVariable Long orderId, Model model) {
        String captcha = CaptchaUtil.generateText();

        PaymentSession session = paymentSessionService.createSession(orderId, captcha);

        String base64Image = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(CaptchaUtil.generateImage(captcha), "png", baos);
            base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", 50);
        model.addAttribute("captchaImageUrl", "data:image/png;base64," + base64Image);
        model.addAttribute("token", session.getToken());


        return "WEB-INF/payment";
    }


    @GetMapping("/page")
    public void showPaymentPage(@RequestParam("sessionToken") String sessionToken,
                                  Model model,
                                  HttpServletResponse httpServletResponse,
                                  @Value("static/payment.html") Resource resource)
            throws IOException {

//        throw new RuntimeException("Not yet implemented");
//        httpServletResponse.sendError(404);
//        return null;
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        String s = new String(resource.getInputStream().readAllBytes());
        Optional<PaymentSession> optSession = paymentSessionService.findByToken(sessionToken);

//        if (optSession.isEmpty() || optSession.get().getExpireAt().isBefore(java.time.LocalDateTime.now())) {
//            model.addAttribute("errorMessage", "Invalid or expired payment session.");
//            return "errorPage";
//        }
        PaymentSession session = optSession.get();

        String captchaBase64Image = generateCaptchaImageBase64(session.getCaptchaCode());
        s = s.replace("${token}", sessionToken)
                .replace("${captchaImage}", captchaBase64Image)
                .replace("${expiresAt}", session.getExpireAt().toString())
                .replace("${orderId}", session.getOrderId().toString());
//        model.addAttribute("token", session.getToken());
//        model.addAttribute("captchaImage", captchaBase64Image);
//        model.addAttribute("expiresAt", session.getExpireAt().toString());
//        model.addAttribute("orderId", session.getOrderId());

        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(s);
        writer.flush();

//        return "payment";
    }

    private String generateCaptchaImageBase64(String captchaCode) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(CaptchaUtil.generateImage(captchaCode), "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
