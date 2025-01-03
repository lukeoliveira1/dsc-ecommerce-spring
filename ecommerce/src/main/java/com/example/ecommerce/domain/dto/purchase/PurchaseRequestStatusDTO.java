package com.example.ecommerce.domain.dto.purchase;

import com.example.ecommerce.domain.enums.OrderStatus;

public record PurchaseRequestStatusDTO(OrderStatus orderStatus) {
}
