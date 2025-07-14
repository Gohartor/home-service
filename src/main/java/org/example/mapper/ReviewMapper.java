package org.example.mapper;

import org.example.dto.expert.OrderRatingDto;
import org.example.entity.Review;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "rating", source = "rating")
    OrderRatingDto toOrderRatingDto(Review review);


    List<OrderRatingDto> toOrderRatingDtoList(List<Review> reviews);
}
