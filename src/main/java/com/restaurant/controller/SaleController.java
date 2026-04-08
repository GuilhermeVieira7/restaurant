package com.restaurant.controller;

import com.restaurant.dto.request.SaleRequest;
import com.restaurant.dto.response.SaleResponse;
import com.restaurant.entity.User;
import com.restaurant.service.SaleService;
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
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponse> finalizeSale(@Valid @RequestBody SaleRequest request,
                                                     @AuthenticationPrincipal User cashier) {
        SaleResponse response = saleService.finalizeSale(request, cashier);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> findAll() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<SaleResponse>> findByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(saleService.findByDate(date));
    }

    @GetMapping("/period")
    public ResponseEntity<List<SaleResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(saleService.findByDateRange(startDate, endDate));
    }
}
