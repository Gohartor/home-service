package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.customer.ReviewCreateDto;
import org.example.dto.customer.ReviewDto;
import org.example.dto.expert.ExpertRatingDto;
import org.example.dto.order.OrderRatingDto;
import org.example.entity.Order;
import org.example.entity.Review;
import org.example.entity.enumerator.ServiceStatus;
import org.example.mapper.ReviewMapper;
import org.example.repository.ReviewRepository;
import org.example.service.OrderService;
import org.example.service.ReviewService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper reviewMapper;
    private final OrderService orderService;

    public ReviewServiceImpl(ReviewRepository repository, ReviewMapper reviewMapper, OrderService orderService) {
        this.repository = repository;
        this.reviewMapper = reviewMapper;
        this.orderService = orderService;
    }

    @Override
    public Review save(Review entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Review> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }


    public ExpertRatingDto getExpertAverageRating(Long expertId) {
        Double avg = repository.findAverageRatingByExpertId(expertId);
        int count = repository.countByExpertId(expertId);
        return new ExpertRatingDto(avg != null ? avg : 0.0, count);
    }


    public OrderRatingDto getOrderRating(Long expertId, Long orderId) {
        Review review = repository.findByOrderIdAndExpertId(orderId, expertId)
                .orElseThrow(() -> new NoSuchElementException("Rating not found for this order & expert"));

        return reviewMapper.toOrderRatingDto(review);
    }




    @Override
    public ReviewDto addReview(Long customerId, ReviewCreateDto dto) {


        Order order = orderService.findById(dto.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId))
            throw new AccessDeniedException("Not Your Order!");
        if (!order.getStatus().equals(ServiceStatus.COMPLETED))
            throw new IllegalStateException("Order not completed");
        if (repository.findByOrderId(order.getId()).isPresent())
            throw new IllegalStateException("Review already exists for this order");

        Review review = new Review();
        review.setOrder(order);
        review.setCustomer(order.getCustomer());
        review.setExpert(order.getExpert());
        review.setRating(dto.rating());
        review.setComment(dto.comment() != null ? dto.comment() : null);

        var saved = repository.save(review);
        return reviewMapper.toDto(saved);
    }



    @Override
    public Optional<ReviewDto> getReviewByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .map(reviewMapper::toDto);
    }
}