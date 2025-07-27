package org.example.repository;

import org.example.entity.EmailVerificationToken;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {


    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

    void deleteByUserAndIsUsedFalseAndExpiresAtAfter(User user, ZonedDateTime now);

    Long countByUser(User user);

    void deleteByUser(User user);

    @Modifying
    @Query("delete from EmailVerificationToken t where t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);


}
