package com.restaurant.service;

import com.restaurant.dto.request.SaleRequest;
import com.restaurant.dto.response.SaleResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.Sale;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final OrderService orderService;

    @Transactional
    public SaleResponse create(SaleRequest request) {
        Order order = orderService.getOrderById(request.getOrderId());

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot create a sale for a cancelled order");
        }
        if (saleRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BusinessException("A sale already exists for order: " + order.getId());
        }

        BigDecimal total = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());

        Sale sale = Sale.builder()
                .order(order)
                .totalAmount(total)
                .paymentMethod(paymentMethod)
                .build();

        order.setStatus(OrderStatus.DELIVERED);

        return toResponse(saleRepository.save(sale));
    }

    public List<SaleResponse> findAll() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return toResponse(sale);
    }

    private SaleResponse toResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .orderId(sale.getOrder().getId())
                .tableNumber(sale.getOrder().getTableNumber())
                .totalAmount(sale.getTotalAmount())
                .paymentMethod(sale.getPaymentMethod().name())
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
