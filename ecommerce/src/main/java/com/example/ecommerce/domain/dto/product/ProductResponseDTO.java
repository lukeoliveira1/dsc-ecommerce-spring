package com.example.ecommerce.domain.dto.product;

import com.example.ecommerce.domain.dto.category.CategoryResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(Long id, String name, String description, BigDecimal value, Integer inventory, List<CategoryResponseDTO> categories) {
}
