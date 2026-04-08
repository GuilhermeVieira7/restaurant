package com.restaurant.dto.response;

import com.restaurant.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {

    private Long id;
    private Long orderId;
    private String cashierName;
    private PaymentMethod paymentMethod;
    private BigDecimal totalValue;
    private LocalDateTime saleDate;
}
