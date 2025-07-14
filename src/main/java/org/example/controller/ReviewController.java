package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.expert.ExpertRatingDto;
import org.example.dto.expert.OrderRatingDto;
import org.example.entity.User;
import org.example.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expert/ratings")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/average")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<ExpertRatingDto> getAverage(
            @AuthenticationPrincipal User principal) {
        ExpertRatingDto dto = reviewService.getExpertAverageRating(principal.getId());
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<OrderRatingDto> getOrderRating(
            @AuthenticationPrincipal User principal,
            @PathVariable Long orderId) {
        OrderRatingDto dto = reviewService.getOrderRating(principal.getId(), orderId);
        return ResponseEntity.ok(dto);
    }
}
