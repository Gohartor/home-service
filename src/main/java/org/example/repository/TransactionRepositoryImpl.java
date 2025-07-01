package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class TransactionRepositoryImpl
        extends BaseRepositoryImpl<Transaction, Long>
        implements TransactionRepository  {

    public TransactionRepositoryImpl() {
        this.domainClass = Transaction.class;
    }
}
