package org.example.service;

import jakarta.transaction.Transactional;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl
        extends BaseServiceImpl<Order, Long, OrderRepository>
        implements OrderService {

    public OrderServiceImpl(OrderRepository repository) {
        super(repository);
    }



}