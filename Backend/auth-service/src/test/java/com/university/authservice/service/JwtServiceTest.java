package com.university.authservice.service;

import com.university.authservice.domain.Role;
import com.university.authservice.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "test-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@test.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractEmail_FromValidToken_ReturnsEmail() {
        String token = jwtService.generateToken(user);

        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("john@test.com");
    }

    @Test
    void extractAllClaims_FromValidToken_ReturnsClaims() {
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.getSubject()).isEqualTo("john@test.com");
        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
        assertThat(claims.get("fullName", String.class)).isEqualTo("John Doe");
    }

    @Test
    void isTokenValid_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_WithInvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtService.isTokenValid(invalidToken);

        assertThat(isValid).isFalse();
    }
}