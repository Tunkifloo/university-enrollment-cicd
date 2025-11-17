package com.university.authservice.service;

import com.university.authservice.domain.Role;
import com.university.authservice.domain.User;
import com.university.authservice.dto.*;
import com.university.authservice.exception.InvalidCredentialsException;
import com.university.authservice.exception.UserAlreadyExistsException;
import com.university.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${rabbitmq.exchange.email}")
    private String emailExchange;

    @Value("${rabbitmq.routing-key.email}")
    private String emailRoutingKey;

    @Value("${kafka.topics.audit}")
    private String auditTopic;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Intentando registrar usuario: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el email: " + request.getEmail()
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);

        // Enviar email de bienvenida
        sendWelcomeEmail(savedUser);

        //  PUBLICAR EVENTO EN KAFKA
        publishUserRegisteredEvent(savedUser);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Intentando login para: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Email o contraseña incorrectos"
                ));

        if (!user.getActive()) {
            throw new InvalidCredentialsException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        log.info("Login exitoso para: {}", user.getEmail());

        String token = jwtService.generateToken(user);

        //  PUBLICAR EVENTO DE LOGIN EN KAFKA
        publishLoginEvent(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    private void sendWelcomeEmail(User user) {
        try {
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(user.getEmail())
                    .subject("Bienvenido al Sistema de Matrículas")
                    .body(buildWelcomeEmailBody(user.getFullName()))
                    .userName(user.getFullName())
                    .build();

            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, emailMessage);

            log.info("Mensaje de email enviado a RabbitMQ para: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar mensaje a RabbitMQ: {}", e.getMessage());
        }
    }

    //  Publicar evento de registro en Kafka
    private void publishUserRegisteredEvent(User user) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType("USER_REGISTERED")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .action("Usuario registrado")
                    .details("Nuevo usuario registrado: " + user.getFullName())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("USER")
                    .entityId(user.getId())
                    .build();

            kafkaTemplate.send(userRegisteredTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento de registro publicado en Kafka para: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error al publicar evento en Kafka: {}", e.getMessage());
        }
    }

    //  Publicar evento de login en Kafka
    private void publishLoginEvent(User user) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType("USER_LOGIN")
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .action("Usuario inició sesión")
                    .details("Login exitoso para: " + user.getFullName())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("USER")
                    .entityId(user.getId())
                    .build();

            kafkaTemplate.send(auditTopic, event);

            log.info("Evento de login publicado en Kafka para: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error al publicar evento de login en Kafka: {}", e.getMessage());
        }
    }

    private String buildWelcomeEmailBody(String fullName) {
        return String.format("""
                Hola %s,
                
                ¡Bienvenido al Sistema de Matrículas Universitarias!
                
                Tu cuenta ha sido creada exitosamente. Ahora puedes acceder a todas las funcionalidades del sistema.
                
                Si tienes alguna pregunta, no dudes en contactarnos.
                
                Saludos,
                Equipo de Matrículas
                """, fullName);
    }
}