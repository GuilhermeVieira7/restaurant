package com.restaurant.service;

import com.restaurant.dto.request.SaleRequest;
import com.restaurant.dto.response.SaleResponse;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.entity.Product;
import com.restaurant.entity.Sale;
import com.restaurant.entity.User;
import com.restaurant.enums.OrderStatus;
import com.restaurant.exception.BusinessException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;

    @Transactional
    public SaleResponse finalizeSale(SaleRequest request, User cashier) {
        Order order = orderService.findOrderById(request.getOrderId());

        // Verificar se o pedido está com status READY
        if (order.getStatus() != OrderStatus.READY) {
            throw new BusinessException(
                    "O pedido deve estar com status 'PRONTO' para ser finalizado. Status atual: " + order.getStatus());
        }

        // Verificar se já existe venda para este pedido
        if (saleRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BusinessException("Já existe uma venda registrada para o pedido ID: " + order.getId());
        }

        // Verificar e atualizar estoque
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "Estoque insuficiente para o produto '" + product.getName() +
                                "'. Disponível: " + product.getStockQuantity() +
                                ", Necessário: " + item.getQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // Atualizar status do pedido para FINISHED
        order.setStatus(OrderStatus.FINISHED);

        // Criar a venda
        Sale sale = Sale.builder()
                .order(order)
                .cashier(cashier)
                .paymentMethod(request.getPaymentMethod())
                .totalValue(order.getTotalValue())
                .build();

        sale = saleRepository.save(sale);
        return toResponse(sale);
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
        return toResponse(sale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findAll() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return saleRepository.findBySaleDateBetween(start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return saleRepository.findBySaleDateBetween(start, end).stream()
                .map(this::toResponse)
                .toList();
    }

    private SaleResponse toResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .orderId(sale.getOrder().getId())
                .cashierName(sale.getCashier().getName())
                .paymentMethod(sale.getPaymentMethod())
                .totalValue(sale.getTotalValue())
                .saleDate(sale.getSaleDate())
                .build();
    }
}
