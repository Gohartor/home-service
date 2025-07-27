package org.example.service;

import org.example.entity.EmailVerificationToken;
import org.example.entity.User;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public interface EmailVerificationTokenService {


    Optional<EmailVerificationToken> findByToken(String token);

    EmailVerificationToken save(EmailVerificationToken entity);

    Optional<EmailVerificationToken> findByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

    void deleteByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

    void sendEmailVerificationLink(User user);

    void verifyEmail(String token);

    void deleteByUser(User user);

}
