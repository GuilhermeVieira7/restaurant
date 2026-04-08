package com.restaurant.repository;

import com.restaurant.entity.Order;
import com.restaurant.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserId(Long userId);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
}
