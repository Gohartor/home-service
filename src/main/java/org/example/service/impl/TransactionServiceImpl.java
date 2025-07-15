package org.example.service.impl;

import org.example.dto.transaction.TransactionDto;
import org.example.entity.Transaction;
import org.example.mapper.TransactionMapper;
import org.example.repository.TransactionRepository;
import org.example.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository repository, TransactionMapper transactionMapper) {
        this.repository = repository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Transaction save(Transaction entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Transaction> findAll() {
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

    @Override
    public Page<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId, Pageable pageable) {
        return repository.findAllByWalletIdOrderByCreateDateDesc(walletId, pageable);
    }

    @Override
    public List<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId) {
        return repository.findAllByWalletIdOrderByCreateDateDesc(walletId);
    }

    @Override
    public TransactionDto create(Transaction transaction) {
        Transaction saved = repository.save(transaction);
        return transactionMapper.toDto(saved);
    }

    @Override
    public List<TransactionDto> getByWallet(Long walletId) {
        List<Transaction> list = repository.findAllByWalletIdOrderByCreateDateDesc(walletId);
        return transactionMapper.toDtoList(list);
    }

}