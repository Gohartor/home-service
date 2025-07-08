package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.repository.base.BaseRepository;

import java.util.List;

public interface TransactionRepository
        extends BaseRepository<Transaction, Long> {

    List<Transaction> findByWallet_Id(Long walletId);

    List<Transaction> findByType(String type);

}
