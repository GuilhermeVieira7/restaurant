package com.restaurant.config;

import com.restaurant.entity.Product;
import com.restaurant.entity.User;
import com.restaurant.enums.UserRole;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadUsers();
        loadProducts();
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@restaurant.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build());

            userRepository.save(User.builder()
                    .name("Garcom")
                    .email("garcom@restaurant.com")
                    .password(passwordEncoder.encode("garcom123"))
                    .role(UserRole.WAITER)
                    .active(true)
                    .build());

            userRepository.save(User.builder()
                    .name("Cozinha")
                    .email("cozinha@restaurant.com")
                    .password(passwordEncoder.encode("cozinha123"))
                    .role(UserRole.KITCHEN)
                    .active(true)
                    .build());

            log.info("Default users created");
        }
    }

    private void loadProducts() {
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("X-Burguer")
                    .description("Hamburguer com queijo e alface")
                    .price(new BigDecimal("25.90"))
                    .available(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Pizza Margherita")
                    .description("Pizza com molho de tomate, mozzarella e manjericao")
                    .price(new BigDecimal("45.00"))
                    .available(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Porcao de Batata Frita")
                    .description("Batata frita crocante temperada")
                    .price(new BigDecimal("18.50"))
                    .available(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Refrigerante Lata")
                    .description("Refrigerante gelado 350ml")
                    .price(new BigDecimal("6.00"))
                    .available(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Suco Natural")
                    .description("Suco de fruta natural 500ml")
                    .price(new BigDecimal("9.00"))
                    .available(true)
                    .build());

            log.info("Default products created");
        }
    }
}
