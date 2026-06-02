package com.example.artsphere.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import com.example.artsphere.backend.security.JwtAuthenticationFilter;

/**
 * Konfiguracja zabezpieczeń aplikacji backendowej.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Konstruktor domyślny.
     */
    public SecurityConfig() {}

    /**
     * Definiuje łańcuch filtrów bezpieczeństwa dla aplikacji.
     * Konfiguruje tryb stateless, rejestruje filtr JWT i określa,
     * które endpointy są publiczne.
     *
     * @param http obiekt konfiguracji bezpieczeństwa Spring Security.
     * @param jwtAuthenticationFilter filtr weryfikujący access token JWT.
     * @return zbudowany łańcuch filtrów bezpieczeństwa.
     * @throws Exception gdy konfiguracja zabezpieczeń nie może zostać zbudowana.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
