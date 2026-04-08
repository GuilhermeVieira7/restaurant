package com.restaurant.service;

import com.restaurant.dto.OrderItemRequest;
import com.restaurant.dto.OrderRequest;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.Product;
import com.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public List<Order> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
    }

    @Transactional
    public Order create(OrderRequest request) {
        Order order = Order.builder()
                .tableNumber(request.getTableNumber())
                .observations(request.getObservations())
                .status(Order.OrderStatus.PENDENTE)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.findById(itemRequest.getProductId());

            if (!product.isAvailable()) {
                throw new IllegalArgumentException("Produto não disponível: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .observations(itemRequest.getObservations())
                    .build();

            order.getItems().add(item);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long id, Order.OrderStatus newStatus) {
        Order order = findById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }
}
