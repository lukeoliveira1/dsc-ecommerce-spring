package com.example.ecommerce.domain.dto.product;

import java.math.BigDecimal;


public record ProductRequestDTO(String name, String description, BigDecimal value, Integer inventory) {
}