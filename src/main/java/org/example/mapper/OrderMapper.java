package org.example.mapper;


import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.dto.order.OrderDetailDto;
import org.example.dto.order.OrderSummaryDto;
import org.example.entity.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "customerFullName", expression = "java(order.getCustomer() != null ? order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName() : null)")
    ExpertOrderSummaryDto fromOrderToExpertOrderSummaryDto(Order order);


    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "customerFullName", expression = "java(order.getCustomer() != null ? order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName() : null)")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "customerProfilePhoto", source = "customer.profilePhoto")
    ExpertOrderDetailDto fromOrderToExpertOrderDetailDto(Order order);


    List<ExpertOrderSummaryDto> fromOrderListToExpertOrderSummaryDtoList(List<Order> orders);



    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "expertFullName", expression = "java(getFullName(order.getExpert()))")
    @Mapping(target = "customerFullName", expression = "java(getFullName(order.getCustomer()))")
    OrderSummaryDto fromOrderToOrderSummaryDto(Order order);

    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "expertFullName", expression = "java(getFullName(order.getExpert()))")
    @Mapping(target = "customerFullName", expression = "java(getFullName(order.getCustomer()))")
    OrderDetailDto fromOrderToOrderDetailDto(Order order);

    List<OrderSummaryDto> fromOrderListToOrderSummaryDtoList(List<Order> orders);





    default String getFullName(org.example.entity.User user) {
        if (user == null) return null;
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return (first + " " + last).trim();
    }
}
