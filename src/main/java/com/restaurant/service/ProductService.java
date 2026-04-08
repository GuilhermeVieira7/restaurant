package com.restaurant.service;

import com.restaurant.dto.request.ProductRequest;
import com.restaurant.dto.response.ProductResponse;
import com.restaurant.entity.Product;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .active(true)
                .build();

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findProductById(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
