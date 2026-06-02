package com.example.artsphere.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import com.example.artsphere.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Konfiguracja zabezpieczeń aplikacji backendowej.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Konstruktor domyślny.
     */
    public SecurityConfig() {
    }

    /**
     * Definiuje łańcuch filtrów bezpieczeństwa dla aplikacji.
     * Konfiguruje tryb stateless, rejestruje filtr JWT i określa,
     * które endpointy są publiczne.
     *
     * @param http                    obiekt konfiguracji bezpieczeństwa Spring Security.
     * @param jwtAuthenticationFilter filtr weryfikujący access token JWT.
     * @return zbudowany łańcuch filtrów bezpieczeństwa.
     * @throws Exception gdy konfiguracja zabezpieczeń nie może zostać zbudowana.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/artworks/**").permitAll()
                        .requestMatchers("/api/follows/**").permitAll()
                        .requestMatchers("/api/support/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/orders/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Brak autoryzacji: " + authException.getMessage());
                        })
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Konfiguracja CORS (Cross-Origin Resource Sharing).
     * Zezwala na połączenia z dowolnego źródła i obsługuje nagłówki autoryzacji.
     *
     * @return obiekt konfiguracji CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*")); // Zezwól na wszystkie nagłówki
        configuration.setExposedHeaders(Arrays.asList("Authorization", "x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}