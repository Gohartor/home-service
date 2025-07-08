package org.example.repository;

import org.example.entity.Order;
import org.example.entity.User;
import org.example.entity.enumerator.ServiceStatus;
import org.example.repository.base.BaseRepository;
import org.springframework.data.domain.Limit;

import java.util.List;

public interface OrderRepository
        extends BaseRepository<Order, Long> {


    List<Order> findByServiceId(Long serviceId);
}
