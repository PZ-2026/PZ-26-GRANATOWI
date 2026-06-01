package com.example.artsphere.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtr uwierzytelniający żądania na podstawie access tokenu JWT.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    /**
     * Tworzy filtr JWT z serwisem do walidacji i parsowania tokenów.
     *
     * @param jwtService serwis obsługi tokenów JWT.
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Weryfikuje nagłówek Authorization i, jeśli zawiera poprawny access token,
     * ustawia kontekst bezpieczeństwa dla aktualnego żądania.
     *
     * @param request żądanie HTTP.
     * @param response odpowiedź HTTP.
     * @param filterChain łańcuch filtrów.
     * @throws ServletException gdy wystąpi błąd filtrowania.
     * @throws IOException gdy wystąpi błąd IO podczas filtrowania.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            if (jwtService.isValid(token) && "access".equals(jwtService.getTokenType(token))) {
                String username = jwtService.getUsername(token);
                String role = jwtService.getRole(token);
                SimpleGrantedAuthority authority = role != null && !role.isBlank()
                        ? new SimpleGrantedAuthority("ROLE_" + role)
                        : null;
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authority != null ? Collections.singletonList(authority) : Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
