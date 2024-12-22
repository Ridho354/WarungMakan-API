package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.SearchOrderRequest;
import com.enigma.wmb_api.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> getSpecification(SearchOrderRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), request.getCustomerId()));
            }

            if (request.getOrderId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getOrderId()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(cb.between(root.get("orderDate"),
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().plusDays(1).atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Order> getCustomerOrdersSpecification(String customerId) {
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }
}