package com.example.ecommerce.domain.dto.product;

import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;

import java.math.BigDecimal;

import java.util.Set;

public record ProductResponseDTO(Long id, String name, String description,
                                 BigDecimal value, Integer inventory,
                                 Set<CategoryResponseDTO> categories) {
}
