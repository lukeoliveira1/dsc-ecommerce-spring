package com.example.ecommerce.domain.dto.product;

import java.math.BigDecimal;

public record ProductRequestPutDTO(String name, String description, BigDecimal value) {
}
