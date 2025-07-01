package org.example.repository;

import org.example.entity.Order;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class ReviewRepositoryImpl
        extends BaseRepositoryImpl<Order, Long>
        implements OrderRepository  {


}
