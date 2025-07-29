package org.example.specification;


import org.example.dto.order.OrderHistoryFilterDto;
import org.example.entity.Order;
import org.example.entity.enumerator.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> filter(OrderHistoryFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.startDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createDate"), filter.startDate().atStartOfDay()));
            }

            if (filter.endDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createDate"), filter.endDate().atTime(23, 59, 59)));
            }
//            System.out.println("filter.status() = " + filter.status());
//            System.out.println("OrderHistoryFilterDto: " + filter);
            if (filter.status() != null && !filter.status().isBlank()) {
                predicates.add(
                        cb.equal(root.get("status"), OrderStatus.valueOf(filter.status())));
            }


            if (filter.serviceName() != null && !filter.serviceName().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("service").get("name")), "%" + filter.serviceName().toLowerCase() + "%"));
            }

            if (filter.expertId() != null) {
                predicates.add(
                        cb.equal(root.get("expert").get("id"), filter.expertId()));
            }

            if (filter.customerId() != null) {
                predicates.add(
                        cb.equal(root.get("customer").get("id"), filter.customerId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
