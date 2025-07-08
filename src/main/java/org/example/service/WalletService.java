package org.example.service;

import org.example.entity.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    Wallet save(Wallet entity);
    Optional<Wallet> findById(Long id);
    List<Wallet> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}