package org.ukhanov.userbase.manager.admin;

import org.springframework.data.jpa.domain.Specification;
import org.ukhanov.userbase.user.model.User;

import java.time.LocalDate;

public class UserSpecs {
    public static Specification<User> hasName (String name) {
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("username"), "%" + name + "%"));


    }

    public static Specification<User> createdBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return cb.between(
                        root.get("createdAt"),
                        from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay()
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
            }

            return cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay());
        };
    }

}
