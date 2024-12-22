package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.constant.MenuCategory;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuSpecification {
    public static Specification<Menu> getSpecification(SearchMenuRequest searchMenuRequest) {
        return new Specification<Menu>() {
            public Predicate toPredicate(Root<Menu> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (StringUtils.hasText(searchMenuRequest.getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchMenuRequest.getName().toLowerCase() + "%"));
                }

                if (StringUtils.hasText(searchMenuRequest.getCategory())) {
                    try {
                        predicates.add(cb.equal(root.get("category"), MenuCategory.fromValue(searchMenuRequest.getCategory())));
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Invalid category: " + searchMenuRequest.getCategory());
                    }
                }

                // Filter by availability
                if (Boolean.TRUE.equals(searchMenuRequest.getIsReady())) {
                    predicates.add(cb.equal(root.get("isAvailable"), true));
                    predicates.add(cb.greaterThan(root.get("stock"), 0));
                }

                // Filter by price range
                Long minPrice = searchMenuRequest.getMinPrice();
                Long maxPrice = searchMenuRequest.getMaxPrice();
                if (minPrice != null && maxPrice != null) {
                    predicates.add(cb.between(root.get("price"), minPrice, maxPrice));
                } else if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                } else if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
