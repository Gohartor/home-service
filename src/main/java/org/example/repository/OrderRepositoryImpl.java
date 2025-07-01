package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.Order;
import org.example.entity.Service;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class OrderRepositoryImpl
        extends BaseRepositoryImpl<Order, Long>
        implements OrderRepository  {


    public OrderRepositoryImpl() {
        this.domainClass = Order.class;
    }
}
