package org.example.service;

import org.example.entity.Order;
import org.example.entity.Review;
import org.example.repository.OrderRepository;
import org.example.repository.ReviewRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl
        extends BaseServiceImpl<Review, Long, ReviewRepository>
        implements ReviewService {

    public ReviewServiceImpl(ReviewRepository repository) {
        super(repository);
    }



}