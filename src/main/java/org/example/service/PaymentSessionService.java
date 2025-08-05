package org.example.service;

import org.example.entity.PaymentSession;

import java.util.Optional;

public interface PaymentSessionService {
    PaymentSession createSession(Long orderId, String captchaCode);
    Optional<PaymentSession> findByToken(String token);
    void markPaid(PaymentSession session);
}
