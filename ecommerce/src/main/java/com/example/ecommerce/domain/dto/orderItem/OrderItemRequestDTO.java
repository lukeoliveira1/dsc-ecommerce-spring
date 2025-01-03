package com.example.ecommerce.domain.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
        Integer quantity,
        BigDecimal unityValue,
        Long idProduct
) {}
