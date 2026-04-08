package com.restaurant.config;

import com.restaurant.entity.User;
import com.restaurant.enums.UserRole;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("Administrador")
                    .email("admin@restaurant.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("Usuário administrador padrão criado: admin@restaurant.com / admin123");
        }
    }
}
