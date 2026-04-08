package com.restaurant.service;

import com.restaurant.dto.ProductRequest;
import com.restaurant.entity.Category;
import com.restaurant.entity.Product;
import com.restaurant.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAvailable() {
        return productRepository.findByAvailableTrue();
    }

    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    public Product create(ProductRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryService.findById(request.getCategoryId());
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .available(request.isAvailable())
                .category(category)
                .build();
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAvailable(request.isAvailable());

        if (request.getCategoryId() != null) {
            Category category = categoryService.findById(request.getCategoryId());
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
