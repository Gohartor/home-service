package org.example.service.impl;

import org.example.dto.transaction.TransactionDto;
import org.example.entity.Transaction;
import org.example.mapper.TransactionMapper;
import org.example.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionRepository repository;
    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    void findAllByWalletId_withPageable_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Transaction tx = new Transaction();
        Page<Transaction> txPage = new PageImpl<>(List.of(tx));
        when(repository.findAllByWalletIdOrderByCreateDateDesc(1L, pageable)).thenReturn(txPage);

        Page<Transaction> result = transactionService.findAllByWalletIdOrderByCreateDateDesc(1L, pageable);

        assertEquals(txPage, result);
        verify(repository).findAllByWalletIdOrderByCreateDateDesc(1L, pageable);
    }

    @Test
    void findAllByWalletId_withoutPageable_shouldReturnList() {
        List<Transaction> txList = List.of(new Transaction());
        when(repository.findAllByWalletIdOrderByCreateDateDesc(1L)).thenReturn(txList);

        List<Transaction> result = transactionService.findAllByWalletIdOrderByCreateDateDesc(1L);

        assertEquals(txList, result);
        verify(repository).findAllByWalletIdOrderByCreateDateDesc(1L);
    }

    @Test
    void create_shouldSaveTransactionAndReturnDtoWithFields() {
        Transaction tx = new Transaction();
        Transaction saved = new Transaction();
        saved.setId(10L);

        TransactionDto dto = new TransactionDto(
                10L,
                150.0,
                "WALLET_CHARGE",
                99L,
                5L,
                ZonedDateTime.now()
        );

        when(repository.save(tx)).thenReturn(saved);
        when(transactionMapper.toDto(saved)).thenReturn(dto);

        TransactionDto result = transactionService.create(tx);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(150.0, result.amount());
        assertEquals("WALLET_CHARGE", result.type());
        assertEquals(99L, result.relatedOrderId());
        assertEquals(5L, result.walletId());
        assertNotNull(result.createdAt());

        verify(repository).save(tx);
        verify(transactionMapper).toDto(saved);
    }

    @Test
    void getByWallet_shouldReturnMappedListWithExactFields() {
        List<Transaction> txList = List.of(new Transaction());
        TransactionDto dto = new TransactionDto(
                1L,
                200.0,
                "ORDER_PAYMENT",
                12L,
                3L,
                ZonedDateTime.now()
        );

        List<TransactionDto> dtoList = List.of(dto);

        when(repository.findAllByWalletIdOrderByCreateDateDesc(44L)).thenReturn(txList);
        when(transactionMapper.toDtoList(txList)).thenReturn(dtoList);

        List<TransactionDto> result = transactionService.getByWallet(44L);

        assertEquals(1, result.size());
        TransactionDto actual = result.get(0);
        assertEquals(1L, actual.id());
        assertEquals(200.0, actual.amount());
        assertEquals("ORDER_PAYMENT", actual.type());
        assertEquals(12L, actual.relatedOrderId());
        assertEquals(3L, actual.walletId());
        assertNotNull(actual.createdAt());

        verify(repository).findAllByWalletIdOrderByCreateDateDesc(44L);
        verify(transactionMapper).toDtoList(txList);
    }
}

