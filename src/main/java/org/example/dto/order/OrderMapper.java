package org.example.dto.order;

import org.example.dto.expert.ExpertOrderDetailDto;
import org.example.dto.expert.ExpertOrderSummaryDto;
import org.example.entity.Order;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "customerFullName", expression = "java(order.getCustomer() != null ? order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName() : null)")
    ExpertOrderSummaryDto toSummaryDto(Order order);


    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "customerFullName", expression = "java(order.getCustomer() != null ? order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName() : null)")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "customerProfilePhoto", source = "customer.profilePhoto")
    ExpertOrderDetailDto toDetailDto(Order order);


    List<ExpertOrderSummaryDto> toSummaryDtoList(List<Order> orders);
}
