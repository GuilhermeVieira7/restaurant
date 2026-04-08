package com.restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SaleRequest {

    @NotNull
    private Long orderId;

    @NotNull
    @Pattern(regexp = "CASH|CREDIT_CARD|DEBIT_CARD|PIX", flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Payment method must be one of: CASH, CREDIT_CARD, DEBIT_CARD, PIX")
    private String paymentMethod;
}
