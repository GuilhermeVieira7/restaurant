package com.restaurant.dto.request;

import com.restaurant.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

    @NotNull(message = "ID do pedido é obrigatório")
    private Long orderId;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private PaymentMethod paymentMethod;
}
