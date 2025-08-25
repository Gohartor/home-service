package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.transaction.TransactionDto;
import org.example.dto.wallet.WalletBalanceDto;
import org.example.dto.wallet.WalletChargeDto;
import org.example.dto.wallet.WalletDto;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.entity.Wallet;
import org.example.entity.enumerator.TransactionType;
import org.example.mapper.TransactionMapper;
import org.example.mapper.WalletMapper;
import org.example.repository.WalletRepository;
import org.example.service.TransactionService;
import org.example.service.WalletService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    public WalletServiceImpl(WalletRepository repository, TransactionService transactionService, TransactionMapper transactionMapper, WalletMapper walletMapper) {
        this.repository = repository;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.walletMapper = walletMapper;
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

    @Override
    public Optional<Wallet> findByUser_Id(Long userId) {
        return repository.findByUser_Id(userId);
    }

    @Override
    public Wallet createWalletForUser(User user) {
        Optional<Wallet> existingWallet = repository.findByUser_Id(user.getId());
        if (existingWallet.isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for this user.");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0.0);

        return repository.save(wallet);
    }


    public WalletBalanceDto getWalletBalance(Long userId) {
        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        return new WalletBalanceDto(wallet.getBalance());
    }


    public List<TransactionDto> getTransactions(Long userId) {
        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        List<Transaction> txs = transactionService.findAllByWalletIdOrderByCreateDateDesc(wallet.getId());
        return transactionMapper.toDtoList(txs);
    }



    public Page<TransactionDto> getTransactionsPage(Long userId, Pageable pageable) {
        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        Page<Transaction> page = transactionService.findAllByWalletIdOrderByCreateDateDesc(wallet.getId(), pageable);
        return page.map(transactionMapper::toDto);
    }


    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return repository.findByUser_Id(userId);
    }



    @Override
    public WalletDto chargeWallet(Long userId, WalletChargeDto chargeRequest) {

        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + chargeRequest.amount());

        Transaction transaction = new Transaction();
        transaction.setAmount(chargeRequest.amount());
        transaction.setType(TransactionType.WALLET_CHARGE);
        transaction.setWallet(wallet);
        transactionService.save(transaction);

        repository.save(wallet);
        return walletMapper.toDto(wallet);
    }


    @Override
    public WalletDto getWalletByUser(Long userId) {
        return repository.findByUser_Id(userId)
                .map(walletMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));
    }

    @Override
    public List<TransactionDto> getWalletTransactions(Long walletId) {
        List<Transaction> list = transactionService.findAllByWalletIdOrderByCreateDateDesc(walletId);
        return transactionMapper.toDtoList(list);
    }
}