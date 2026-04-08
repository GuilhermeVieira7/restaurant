package com.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SaleResponse {
    private Long id;
    private Long orderId;
    private Integer tableNumber;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime createdAt;
}
