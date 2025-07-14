package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.transaction.TransactionDto;
import org.example.dto.wallet.WalletBalanceDto;
import org.example.entity.Transaction;
import org.example.entity.Wallet;
import org.example.mapper.TransactionMapper;
import org.example.repository.WalletRepository;
import org.example.service.TransactionService;
import org.example.service.WalletService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final TransactionService transactionService;
    private final TransactionMapper mapper;

    public WalletServiceImpl(WalletRepository repository, TransactionService transactionService, TransactionMapper mapper) {
        this.repository = repository;
        this.transactionService = transactionService;
        this.mapper = mapper;
    }

    @Override
    public Wallet save(Wallet entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Wallet> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }


    public WalletBalanceDto getWalletBalance(Long userId) {
        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        return new WalletBalanceDto(wallet.getBalance());
    }


    public List<TransactionDto> getTransactions(Long userId) {
        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        List<Transaction> txs = transactionService.findAllByWalletIdOrderByCreateDateDesc(wallet.getId());
        return mapper.toDtoList(txs);
    }



    public Page<TransactionDto> getTransactionsPage(Long userId, Pageable pageable) {
        Wallet wallet = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        Page<Transaction> page = transactionService.findAllByWalletIdOrderByCreateDateDesc(wallet.getId(), pageable);
        return page.map(mapper::toDto);
    }
}