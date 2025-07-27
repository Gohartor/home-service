package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.EmailVerificationToken;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.service.EmailVerificationTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository repository;


    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken entity) {
        return repository.save(entity);
    }
}
