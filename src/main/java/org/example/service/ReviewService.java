package org.example.service;

import org.example.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review save(Review entity);
    Optional<Review> findById(Long id);
    List<Review> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}