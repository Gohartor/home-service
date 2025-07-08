package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Wallet;
import org.example.repository.base.BaseRepository;

import java.util.Optional;

public interface WalletRepository
        extends BaseRepository<Wallet, Long> {


    Optional<Wallet> findByUserId(Long userId);

}
