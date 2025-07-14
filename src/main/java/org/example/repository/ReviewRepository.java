package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Review;
import org.example.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends BaseRepository<Review, Long> {


    List<Review> findAllByExpertId(Long expertId);

    @Query("select avg(r.rating) from Review r where r.expert.id = :expertId")
    Double findAverageRatingByExpertId(@Param("expertId") Long expertId);

    @Query("select count(r) from Review r where r.expert.id = :expertId")
    int countByExpertId(@Param("expertId") Long expertId);

    Optional<Review> findByOrderIdAndExpertId(Long orderId, Long expertId);

    Optional<Review> findByOrderId(Long orderId);
}
