package com.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Integer tableNumber;
    private String status;
    private List<OrderItemResponse> items;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
