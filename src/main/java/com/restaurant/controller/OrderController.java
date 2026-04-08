package com.restaurant.controller;

import com.restaurant.dto.request.OrderRequest;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request,
                                                @AuthenticationPrincipal User currentUser) {
        OrderResponse response = orderService.createOrder(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                      @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderResponse>> findByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.findByDate(date));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderResponse>> findByStatusAndDate(
            @RequestParam OrderStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.findByStatusAndDate(status, date));
    }
}
