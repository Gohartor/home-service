package org.example.service;

import org.example.dto.transaction.TransactionDto;
import org.example.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction save(Transaction entity);
    Optional<Transaction> findById(Long id);
    List<Transaction> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    Page<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId, Pageable pageable);

    List<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId);

    TransactionDto create(Transaction transaction);

    List<TransactionDto> getByWallet(Long walletId);

}