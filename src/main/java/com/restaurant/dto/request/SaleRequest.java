package com.restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaleRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private String paymentMethod;
}
