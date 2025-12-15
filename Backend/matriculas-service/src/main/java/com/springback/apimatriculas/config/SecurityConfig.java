package com.springback.apimatriculas.config;

import com.springback.apimatriculas.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String FACULTADES_PATH = "/facultades/**";
    private static final String CARRERAS_PATH = "/carreras/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando Security Filter Chain para Matriculas Service");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Health check - público
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Swagger/OpenAPI - públicos
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints públicos de lectura
                        .requestMatchers(HttpMethod.GET, FACULTADES_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, CARRERAS_PATH).permitAll()

                        // Endpoints de escritura - autenticados
                        // Facultades
                        .requestMatchers(HttpMethod.POST, FACULTADES_PATH).authenticated()
                        .requestMatchers(HttpMethod.PUT, FACULTADES_PATH).authenticated()
                        .requestMatchers(HttpMethod.DELETE, FACULTADES_PATH).authenticated()

                        // Carreras
                        .requestMatchers(HttpMethod.POST, CARRERAS_PATH).authenticated()
                        .requestMatchers(HttpMethod.PUT, CARRERAS_PATH).authenticated()
                        .requestMatchers(HttpMethod.DELETE, CARRERAS_PATH).authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}