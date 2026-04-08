package com.restaurant.service;

import com.restaurant.dto.request.ProductRequest;
import com.restaurant.dto.response.ProductResponse;
import com.restaurant.entity.Product;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .build();
        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findAvailable() {
        return productRepository.findByAvailableTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        return toResponse(getProductById(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getAvailable() != null) {
            product.setAvailable(request.getAvailable());
        }
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = getProductById(id);
        product.setAvailable(false);
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .available(product.getAvailable())
                .build();
    }
}
