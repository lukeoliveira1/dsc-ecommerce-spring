package com.example.ecommerce.domain.dto.category;

import com.example.ecommerce.domain.Product;

import java.util.Set;

public record CategoryProductsResponseDTO(
        Long id,
        String name,
        String description,
        Set<Product> products
) {}
