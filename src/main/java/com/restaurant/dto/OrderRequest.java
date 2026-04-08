package com.restaurant.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Integer tableNumber;

    private String observations;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<OrderItemRequest> items;
}
