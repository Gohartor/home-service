package org.example.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface EmailVerificationTokenService {

    Optional<Object> findByToken(String token);
}
