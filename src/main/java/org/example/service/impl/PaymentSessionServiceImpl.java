package org.example.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.entity.PaymentSession;
import org.example.repository.PaymentSessionRepository;
import org.example.service.PaymentSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentSessionServiceImpl implements PaymentSessionService {

    private final PaymentSessionRepository repo;

    @Override
    public PaymentSession createSession(Long orderId, String captchaCode) {
        PaymentSession session = new PaymentSession();
        session.setOrderId(orderId);
        session.setToken(UUID.randomUUID().toString());
        session.setExpireAt(LocalDateTime.now().plusMinutes(10));
        session.setPaid(false);
        session.setCaptchaCode(captchaCode);
        return repo.save(session);
    }

    @Override
    public Optional<PaymentSession> findByToken(String token) {
        Optional<PaymentSession> result = repo.findByToken(token);

        return result;
    }

    @Override
    public void markPaid(PaymentSession session) {
        session.setPaid(true);
        repo.save(session);
    }
}
