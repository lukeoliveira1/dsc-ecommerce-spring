package com.example.ecommerce.domain.dto.orderItem;

import com.example.ecommerce.domain.dto.product.ProductResponseDTO;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Integer quantity,
        BigDecimal unityValue,
        ProductResponseDTO product
) {
}
