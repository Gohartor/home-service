package org.example.service;

import org.example.dto.transaction.TransactionDto;
import org.example.dto.wallet.WalletBalanceDto;
import org.example.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    Wallet save(Wallet entity);
    Optional<Wallet> findById(Long id);
    List<Wallet> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    WalletBalanceDto getWalletBalance(Long userId);
    List<TransactionDto> getTransactions(Long userId);
    Page<TransactionDto> getTransactionsPage(Long userId, Pageable pageable);
}