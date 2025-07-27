package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.EmailVerificationToken;
import org.example.entity.User;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.service.EmailVerificationTokenService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository repository;
    private final EmailService emailService;
    private final UserService userService;


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




    @Override
    @Transactional
    public void sendEmailVerificationLink(User user) {
        repository.deleteByUserId(user.getId());
        repository.flush();

//        System.out.println("Before delete: " + repository.countByUser(user));
//
//        repository.deleteByUserAndIsUsedFalseAndExpiresAtAfter(user, ZonedDateTime.now());
//        System.out.println("After delete: " + repository.countByUser(user));

        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setExpiresAt(ZonedDateTime.now().plusMinutes(2));
        emailVerificationToken.setUsed(false);
        repository.save(emailVerificationToken);

        String link = "http://localhost:8080/experts/verify-email?token=" + token;

        emailService.send(
                user.getEmail(),
                "Verify your email ->Home Service App<-",
                "click on this link:\n" + link
        );
    }



    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("invalid token"));

        if (verificationToken.isUsed() || verificationToken.getExpiresAt().isBefore(ZonedDateTime.now()))
            throw new RuntimeException("token expired");

        verificationToken.setUsed(true);
        User user = verificationToken.getUser();
        user.setEmailVerified(true);

        userService.save(user);
        repository.save(verificationToken);
    }


    @Override
    public void deleteByUser(User user) {
        repository.deleteByUser(user);
    }
}
