package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.repository.base.BaseRepository;

public interface TransactionRepository
        extends BaseRepository<Transaction, Long> {
}
