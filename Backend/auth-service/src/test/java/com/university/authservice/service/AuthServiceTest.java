package com.university.authservice.service;

import com.university.authservice.domain.Role;
import com.university.authservice.domain.User;
import com.university.authservice.dto.AuthResponse;
import com.university.authservice.dto.LoginRequest;
import com.university.authservice.dto.RegisterRequest;
import com.university.authservice.exception.InvalidCredentialsException;
import com.university.authservice.exception.UserAlreadyExistsException;
import com.university.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "emailExchange", "email.exchange");
        ReflectionTestUtils.setField(authService, "emailRoutingKey", "email.routing.key");
        ReflectionTestUtils.setField(authService, "auditTopic", "audit.events");
        ReflectionTestUtils.setField(authService, "userRegisteredTopic", "user.registered");

        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        registerRequest = new RegisterRequest(
                "John Doe",
                "john@test.com",
                "password123"
        );

        loginRequest = new LoginRequest(
                "john@test.com",
                "password123"
        );
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        assertThat(response.getType()).isEqualTo("Bearer");

        verify(userRepository).existsByEmail("john@test.com");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un usuario con el email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getEmail()).isEqualTo("john@test.com");

        verify(userRepository).findByEmail("john@test.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Email o contraseña incorrectos");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Email o contraseña incorrectos");

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_InactiveUser_ThrowsException() {
        user.setActive(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Usuario inactivo");
    }
}