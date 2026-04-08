package com.restaurant.repository;

import com.restaurant.entity.Sale;
import com.restaurant.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByOrderId(Long orderId);

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    List<Sale> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Sale> findByCashierId(Long cashierId);
}
