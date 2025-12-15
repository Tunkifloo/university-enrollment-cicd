package com.university.authservice;

import com.university.authservice.domain.Role;
import com.university.authservice.domain.User;
import com.university.authservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    @Value("${app.security.admin-password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Auth Service iniciado correctamente");
        System.out.println("API: http://localhost:8082");
        System.out.println("========================================\n");
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@university.com";

            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .fullName("Administrador del Sistema")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ROLE_ADMIN)
                        .active(true)
                        .build();

                userRepository.save(admin);

                log.info("════════════════════════════════════════════════════════");
                log.info("Usuario Admin creado exitosamente");
                log.info("════════════════════════════════════════════════════════");
                log.info("Email: {}", adminEmail);
                log.info("Password: [PROTECTED]");
                log.info("Role: {}", Role.ROLE_ADMIN);
                log.info("IMPORTANTE: Las credenciales han sido configuradas externamente");
                log.info("════════════════════════════════════════════════════════");
            } else {
                log.info("✓ Usuario admin ya existe: {}", adminEmail);
            }
        };
    }
}