package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.EmailVerificationToken;
import org.example.entity.User;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.service.EmailVerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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

    @Override
    public Optional<EmailVerificationToken> findByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now) {
        return repository.findByUserAndIsUsedFalseAndExpiresAtAfter(user, now);
    }

    @Override
    public void deleteByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now) {
        repository.deleteByUserAndIsUsedFalseAndExpiresAtAfter(user, now);
    }
}
