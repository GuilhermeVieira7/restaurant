package com.restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull
    @Min(1)
    private Integer tableNumber;

    @NotEmpty
    private List<OrderItemRequest> items;
}
