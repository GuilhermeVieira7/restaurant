package com.restaurant.config;

import com.restaurant.entity.Category;
import com.restaurant.entity.Product;
import com.restaurant.entity.User;
import com.restaurant.repository.CategoryRepository;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@restaurant.com")
                        .role(User.Role.ADMIN)
                        .build();
                userRepository.save(admin);
                log.info("Usuário admin criado: admin / admin123");
            }

            // Garçom user
            if (!userRepository.existsByUsername("garcom")) {
                User garcom = User.builder()
                        .username("garcom")
                        .password(passwordEncoder.encode("garcom123"))
                        .email("garcom@restaurant.com")
                        .role(User.Role.GARCOM)
                        .build();
                userRepository.save(garcom);
                log.info("Usuário garçom criado: garcom / garcom123");
            }

            // Categories
            if (categoryRepository.count() == 0) {
                Category entradas = categoryRepository.save(
                        Category.builder().name("Entradas").description("Pratos para começar").build()
                );
                Category pratos = categoryRepository.save(
                        Category.builder().name("Pratos Principais").description("Pratos principais").build()
                );
                Category bebidas = categoryRepository.save(
                        Category.builder().name("Bebidas").description("Bebidas variadas").build()
                );
                Category sobremesas = categoryRepository.save(
                        Category.builder().name("Sobremesas").description("Doces e sobremesas").build()
                );

                // Products
                productRepository.save(Product.builder()
                        .name("Bruschetta").description("Pão tostado com tomate e manjericão")
                        .price(new BigDecimal("18.90")).available(true).category(entradas).build());
                productRepository.save(Product.builder()
                        .name("Bolinho de Bacalhau").description("6 unidades com aioli")
                        .price(new BigDecimal("24.90")).available(true).category(entradas).build());

                productRepository.save(Product.builder()
                        .name("File Mignon ao Molho Madeira").description("300g com batatas rústicas")
                        .price(new BigDecimal("89.90")).available(true).category(pratos).build());
                productRepository.save(Product.builder()
                        .name("Frango Grelhado").description("Com legumes salteados e arroz")
                        .price(new BigDecimal("52.90")).available(true).category(pratos).build());
                productRepository.save(Product.builder()
                        .name("Risoto de Camarão").description("Com camarões frescos e ervas")
                        .price(new BigDecimal("74.90")).available(true).category(pratos).build());

                productRepository.save(Product.builder()
                        .name("Suco Natural").description("Laranja, limão ou maracujá")
                        .price(new BigDecimal("12.90")).available(true).category(bebidas).build());
                productRepository.save(Product.builder()
                        .name("Água Mineral").description("500ml com ou sem gás")
                        .price(new BigDecimal("6.00")).available(true).category(bebidas).build());
                productRepository.save(Product.builder()
                        .name("Refrigerante").description("Coca-Cola, Guaraná ou Sprite 350ml")
                        .price(new BigDecimal("8.00")).available(true).category(bebidas).build());

                productRepository.save(Product.builder()
                        .name("Pudim de Leite").description("Com calda de caramelo")
                        .price(new BigDecimal("16.90")).available(true).category(sobremesas).build());
                productRepository.save(Product.builder()
                        .name("Mousse de Chocolate").description("Com creme chantilly")
                        .price(new BigDecimal("18.90")).available(true).category(sobremesas).build());

                log.info("Dados iniciais criados com sucesso!");
            }
        };
    }
}
