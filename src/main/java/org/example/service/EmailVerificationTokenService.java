package org.example.service;

import org.example.entity.EmailVerificationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface EmailVerificationTokenService {


    Optional<EmailVerificationToken> findByToken(String token);

    EmailVerificationToken save(EmailVerificationToken entity);

}
