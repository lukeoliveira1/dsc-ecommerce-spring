package com.example.ecommerce.domain.dto.purchase;

import com.example.ecommerce.domain.dto.orderItem.OrderItemRequestDTO;

import java.util.List;

public record PurchaseRequestDTO(
        Long idClient,
        List<OrderItemRequestDTO> items
) {}
