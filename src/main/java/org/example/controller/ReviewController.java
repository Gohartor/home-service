package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.customer.ReviewCreateDto;
import org.example.dto.customer.ReviewDto;
import org.example.dto.expert.ExpertRatingDto;
import org.example.dto.order.OrderRatingDto;
import org.example.entity.User;
import org.example.security.CustomUserDetails;
import org.example.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


//    @GetMapping("/average")
//    @PreAuthorize("hasRole('EXPERT')")
//    public ResponseEntity<ExpertRatingDto> getAverage(
//            @AuthenticationPrincipal User principal) {
//        ExpertRatingDto dto = reviewService.getExpertAverageRating(principal.getId());
//        return ResponseEntity.ok(dto);
//    }


    @GetMapping("/average")
    public ResponseEntity<ExpertRatingDto> getAverage(
            @RequestParam("expertId") Long expertId)
    {
        ExpertRatingDto dto = reviewService.getExpertAverageRating(expertId);
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


//    @PostMapping("/add")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public ResponseEntity<ReviewDto> addReview(@AuthenticationPrincipal User customer,
//                                               @RequestBody ReviewCreateDto dto) {
//        var saved = reviewService.addReview(customer.getId(), dto);
//        return ResponseEntity.ok(saved);
//    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewDto> addReview(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody ReviewCreateDto dto)
    {
        Long customerId = principal.getId();
        return ResponseEntity.ok(reviewService.addReview(customerId, dto));
    }



    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','EXPERT')")
    public ResponseEntity<ReviewDto> getReviewByOrderId(@PathVariable Long orderId) {
        return reviewService.getReviewByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}