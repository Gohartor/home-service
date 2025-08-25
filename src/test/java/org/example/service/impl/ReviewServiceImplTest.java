package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.customer.ReviewCreateDto;
import org.example.dto.customer.ReviewDto;
import org.example.dto.order.OrderRatingDto;
import org.example.entity.Order;
import org.example.entity.Review;
import org.example.entity.User;
import org.example.entity.enumerator.OrderStatus;
import org.example.mapper.ReviewMapper;
import org.example.repository.ReviewRepository;
import org.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    ReviewRepository reviewRepository;
    @Mock ReviewMapper reviewMapper;
    @Mock OrderService orderService;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Test
    void getExpertAverageRating_shouldReturnValues_whenNotNull() {
        when(reviewRepository.findAverageRatingByExpertId(5L)).thenReturn(4.5);
        when(reviewRepository.countByExpertId(5L)).thenReturn(3);

        var result = reviewService.getExpertAverageRating(5L);

        assertEquals(4.5, result.averageRating());
        assertEquals(3, result.totalReviews());
    }

    @Test
    void getExpertAverageRating_shouldReturnZero_whenNull() {
        when(reviewRepository.findAverageRatingByExpertId(5L)).thenReturn(null);
        when(reviewRepository.countByExpertId(5L)).thenReturn(2);

        var result = reviewService.getExpertAverageRating(5L);

        assertEquals(0.0, result.averageRating());
        assertEquals(2, result.totalReviews());
    }

    @Test
    void getOrderRating_success() {
        Review review = new Review();
        OrderRatingDto dto = new OrderRatingDto(4L, 4);
        when(reviewRepository.findByOrderIdAndExpertId(1L, 2L)).thenReturn(Optional.of(review));
        when(reviewMapper.toOrderRatingDto(review)).thenReturn(dto);

        var result = reviewService.getOrderRating(2L, 1L);

        assertEquals(dto, result);
    }

    @Test
    void getOrderRating_notFound() {
        when(reviewRepository.findByOrderIdAndExpertId(any(), any())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> reviewService.getOrderRating(2L, 1L));
    }



    @Test
    void addReview_orderNotFound() {
        when(orderService.findById(99L)).thenReturn(Optional.empty());
        ReviewCreateDto dto = new ReviewCreateDto(99L, 4, "ok");

        assertThrows(EntityNotFoundException.class, () -> reviewService.addReview(1L, dto));
    }

    @Test
    void addReview_notYourOrder() {
        Order order = new Order();
        User diffCustomer = new User(); diffCustomer.setId(5L);
        order.setCustomer(diffCustomer);
        order.setStatus(OrderStatus.COMPLETED);
        when(orderService.findById(anyLong())).thenReturn(Optional.of(order));

        ReviewCreateDto dto = new ReviewCreateDto(1L, 4, "ok");

        assertThrows(AccessDeniedException.class, () -> reviewService.addReview(10L, dto));
    }

    @Test
    void addReview_orderNotCompleted() {
        Long cid = 10L;
        Order order = new Order();
        User customer = new User(); customer.setId(cid);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING_PROPOSAL);

        when(orderService.findById(anyLong())).thenReturn(Optional.of(order));

        ReviewCreateDto dto = new ReviewCreateDto(1L, 4, null);

        assertThrows(IllegalStateException.class, () -> reviewService.addReview(cid, dto));
    }

    @Test
    void addReview_alreadyExists() {
        Long cid = 10L;
        Order order = new Order();
        User customer = new User(); customer.setId(cid);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.COMPLETED);
        order.setId(123L);

        when(orderService.findById(order.getId())).thenReturn(Optional.of(order));
        when(reviewRepository.findByOrderId(order.getId())).thenReturn(Optional.of(new Review()));

        ReviewCreateDto dto = new ReviewCreateDto(order.getId(), 5, null);

        assertThrows(IllegalStateException.class, () -> reviewService.addReview(cid, dto));
    }



    @Test
    void getReviewByOrderId_notFound() {
        when(reviewRepository.findByOrderId(5L)).thenReturn(Optional.empty());

        var result = reviewService.getReviewByOrderId(5L);

        assertTrue(result.isEmpty());
    }
}
