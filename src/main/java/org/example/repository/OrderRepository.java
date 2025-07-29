package org.example.repository;

import org.example.entity.Order;
import org.example.entity.enumerator.OrderStatus;
import org.example.repository.base.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderRepository
        extends BaseRepository<Order, Long> {


    List<Order> findByServiceId(Long serviceId);

    boolean existsByExpert_IdAndStatus(Long customerId, OrderStatus status);

    List<Order> findAllByExpertIdOrderByCreateDateDesc(Long expertId);

    List<Order> findAll(Specification<Order> spec, Sort createdAt);

    List<Order> findByCustomer_Email(String email);
}
