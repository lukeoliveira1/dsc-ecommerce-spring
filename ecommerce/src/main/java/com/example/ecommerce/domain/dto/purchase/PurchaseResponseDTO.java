package com.example.ecommerce.domain.dto.purchase;

import com.example.ecommerce.domain.dto.client.ClientResponseDTO;
import com.example.ecommerce.domain.dto.orderItem.OrderItemResponseDTO;
import com.example.ecommerce.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseResponseDTO(
        Long id,
        LocalDateTime orderDate,
        BigDecimal totalValue,
        OrderStatus orderStatus,
        ClientResponseDTO client,
        List<OrderItemResponseDTO> items
) {
}
