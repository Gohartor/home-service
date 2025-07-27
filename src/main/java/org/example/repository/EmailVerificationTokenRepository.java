package org.example.repository;

import org.example.entity.EmailVerificationToken;
import org.example.service.EmailVerificationTokenService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {


    Optional<EmailVerificationToken> findByToken(String token);
}
