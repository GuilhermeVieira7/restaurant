package com.restaurant.controller;

import com.restaurant.dto.request.OrderRequest;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN')")
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN')")
    public ResponseEntity<List<OrderResponse>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN')")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
