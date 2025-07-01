package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.Order;
import org.example.entity.Review;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class ReviewRepositoryImpl
        extends BaseRepositoryImpl<Review, Long>
        implements ReviewRepository {


    public ReviewRepositoryImpl() {
        this.domainClass = Review.class;
    }

}
