package org.example.service;

import org.example.dto.customer.ReviewCreateDto;
import org.example.dto.customer.ReviewDto;
import org.example.dto.expert.ExpertRatingDto;
import org.example.dto.expert.OrderRatingDto;
import org.example.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review save(Review entity);
    Optional<Review> findById(Long id);
    List<Review> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    ExpertRatingDto getExpertAverageRating(Long expertId);

    OrderRatingDto getOrderRating(Long expertId, Long orderId);

    ReviewDto addReview(Long customerId, ReviewCreateDto dto);

    Optional<ReviewDto> getReviewByOrderId(Long orderId);
}