package com.restaurant.controller;

import com.restaurant.dto.request.SaleRequest;
import com.restaurant.dto.response.SaleResponse;
import com.restaurant.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<List<SaleResponse>> findAll() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<SaleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }
}
