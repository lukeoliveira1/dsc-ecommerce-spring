package com.example.ecommerce.domain.enums;

public enum OrderStatus {
    WAITING("Aguardando informação"),
    CANCELLED("Cancelar pedido"),
    PAID("Pago"),
    SHIPPED("Enviado");

    private String description;

    OrderStatus(String description) {
        this.description = description;
    };

    public String getDescription() {
        return description;
    }
}
