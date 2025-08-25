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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock WalletRepository repository;
    @Mock
    TransactionService transactionService;
    @Mock TransactionMapper transactionMapper;
    @Mock WalletMapper walletMapper;

    @InjectMocks
    WalletServiceImpl walletService;

    @Test
    void createWalletForUser_success() {
        User user = new User(); user.setId(1L);
        when(repository.findByUser_Id(1L)).thenReturn(Optional.empty());

        Wallet saved = new Wallet(); saved.setUser(user);
        when(repository.save(any(Wallet.class))).thenReturn(saved);

        Wallet result = walletService.createWalletForUser(user);

        assertEquals(user, result.getUser());
        assertEquals(0.0, result.getBalance());
        verify(repository).save(any(Wallet.class));
    }

    @Test
    void createWalletForUser_alreadyExists() {
        User user = new User(); user.setId(1L);
        when(repository.findByUser_Id(1L)).thenReturn(Optional.of(new Wallet()));
        assertThrows(IllegalArgumentException.class, () -> walletService.createWalletForUser(user));
    }

    @Test
    void getWalletBalance_success() {
        Wallet wallet = new Wallet(); wallet.setBalance(50.0);
        when(repository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));

        WalletBalanceDto dto = walletService.getWalletBalance(1L);

        assertEquals(50.0, dto.balance());
    }

    @Test
    void getWalletBalance_notFound() {
        when(repository.findByUser_Id(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> walletService.getWalletBalance(1L));
    }



    @Test
    void getTransactions_notFound() {
        when(repository.findByUser_Id(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> walletService.getTransactions(1L));
    }



    @Test
    void getTransactionsPage_notFound() {
        when(repository.findByUser_Id(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> walletService.getTransactionsPage(1L, Pageable.unpaged()));
    }






    @Test
    void getWalletByUser_notFound() {
        when(repository.findByUser_Id(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> walletService.getWalletByUser(1L));
    }


}
