package com.restaurant.service;

import com.restaurant.dto.request.OrderItemRequest;
import com.restaurant.dto.request.OrderRequest;
import com.restaurant.dto.response.OrderItemResponse;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.Product;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = Order.builder()
                .tableNumber(request.getTableNumber())
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            if (!product.getAvailable()) {
                throw new BusinessException("Product not available: " + product.getName());
            }
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .notes(itemRequest.getNotes())
                    .build();
            order.getItems().add(item);
        }

        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> findByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatus(orderStatus).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Long id) {
        return toResponse(getOrderById(id));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = getOrderById(id);
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot change status of a " + order.getStatus() + " order");
        }

        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public void cancel(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel a delivered order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(OrderItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .id(order.getId())
                .tableNumber(order.getTableNumber())
                .status(order.getStatus().name())
                .items(items)
                .total(total)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .notes(item.getNotes())
                .build();
    }
}
