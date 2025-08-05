package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.utility.CaptchaGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @GetMapping("/generate")
    public CaptchaResponse getCaptcha(HttpSession session) {
        String text = CaptchaGenerator.generateText();
        session.setAttribute("captcha", text);
        var image = CaptchaGenerator.generateImage(text);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write captcha", e);
        }
        String base64Img = Base64.getEncoder().encodeToString(baos.toByteArray());

        return new CaptchaResponse("data:image/png;base64," + base64Img);
    }

    public record CaptchaResponse(String imageBase64) {}
}

