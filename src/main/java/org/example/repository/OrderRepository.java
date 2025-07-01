package org.example.repository;

import org.example.entity.Order;
import org.example.entity.User;
import org.example.repository.base.BaseRepository;

public interface OrderRepository
        extends BaseRepository<Order, Long> {
}
