package org.example.service.impl;

import org.example.dto.expert.ExpertRatingDto;
import org.example.dto.expert.OrderRatingDto;
import org.example.entity.Review;
import org.example.mapper.ReviewMapper;
import org.example.repository.ReviewRepository;
import org.example.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository repository, ReviewMapper reviewMapper) {
        this.repository = repository;
        this.reviewMapper = reviewMapper;
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
}