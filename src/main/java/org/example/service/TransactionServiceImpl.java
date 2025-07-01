package org.example.service;

import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.repository.OrderRepository;
import org.example.repository.TransactionRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl
        extends BaseServiceImpl<Transaction, Long, TransactionRepository>
        implements TransactionService {

    public TransactionServiceImpl(TransactionRepository repository) {
        super(repository);
    }



}