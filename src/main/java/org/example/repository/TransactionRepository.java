package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.entity.enumerator.TransactionType;
import org.example.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionRepository
        extends BaseRepository<Transaction, Long> {

    List<Transaction> findByWallet_Id(Long walletId);

    List<Transaction> findByType(TransactionType type);

    Page<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId, Pageable pageable);

    List<Transaction> findAllByWalletIdOrderByCreateDateDesc(Long walletId);


    Page<Transaction> findAllByWallet_Id(Long walletId, Pageable pageable);

    List<Transaction> findAllByRelatedOrderId(Long orderId);

}
