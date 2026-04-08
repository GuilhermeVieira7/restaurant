package com.restaurant.service;

import com.restaurant.dto.request.OrderItemRequest;
import com.restaurant.dto.request.OrderRequest;
import com.restaurant.dto.response.OrderItemResponse;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.Product;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponse createOrder(OrderRequest request, User currentUser) {
        Order order = Order.builder()
                .user(currentUser)
                .status(OrderStatus.IN_PREPARATION)
                .totalValue(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.findProductById(itemRequest.getProductId());

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BusinessException(
                        "Estoque insuficiente para o produto '" + product.getName() +
                                "'. Disponível: " + product.getStockQuantity() +
                                ", Solicitado: " + itemRequest.getQuantity());
            }

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();

            order.addItem(item);
        }

        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId);

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return toResponse(findOrderById(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return orderRepository.findByCreatedAtBetween(start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatusAndDate(OrderStatus status, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return orderRepository.findByStatusAndCreatedAtBetween(status, start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case IN_PREPARATION -> next == OrderStatus.READY;
            case READY -> next == OrderStatus.FINISHED;
            case FINISHED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Transição de status inválida: " + current + " -> " + next);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .status(order.getStatus())
                .totalValue(order.getTotalValue())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
