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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando Security Filter Chain para Matriculas Service");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator y Health - públicos
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/health/**").permitAll()

                        // Swagger/OpenAPI - públicos
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints públicos de lectura
                        .requestMatchers(HttpMethod.GET, "/facultades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carreras/**").permitAll()

                        // Endpoints de escritura - autenticados
                        .requestMatchers(HttpMethod.POST, "/facultades/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/facultades/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/facultades/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/carreras/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/carreras/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/carreras/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}