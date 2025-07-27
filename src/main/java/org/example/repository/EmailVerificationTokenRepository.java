package org.example.repository;

import org.example.entity.EmailVerificationToken;
import org.example.entity.User;
import org.example.service.EmailVerificationTokenService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {


    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

    void deleteByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

}
