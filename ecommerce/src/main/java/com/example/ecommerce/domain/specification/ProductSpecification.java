package com.example.ecommerce.domain.specification;

import com.example.ecommerce.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null :
                        // adiciona filtro LIKE (case-insensitive).
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(
                                "name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null) return null;
            return criteriaBuilder.like(
                    // faz o join com "categories" e acessa o atributo "name"
                    criteriaBuilder.lower(root.join("categories").get("name")),
                    // adiciona filtro LIKE (case-insensitive).
                    "%" + categoryName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasValueGreaterThanOrEqualTo(BigDecimal value) {
        return (root, query, criteriaBuilder) ->
                value == null ? null :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("value"),
                                value);
    }

    public static Specification<Product> hasValueLessThanOrEqualTo(BigDecimal value) {
        return (root, query, criteriaBuilder) ->
                value == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(root.get("value"),
                                value);
    }
}
