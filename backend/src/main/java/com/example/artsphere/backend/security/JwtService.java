package com.example.artsphere.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Serwis odpowiedzialny za generowanie i walidację tokenów JWT.
 */
@Service
public class JwtService {
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey signingKey;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    /**
     * Tworzy serwis JWT na podstawie konfiguracji zewnętrznej.
     *
     * @param secret sekret używany do podpisywania tokenów (min. 32 znaki dla HS256);
     *               domyślnie używany jest placeholder, jeśli zmienna środowiskowa nie jest ustawiona.
     * @param accessTtlMinutes czas życia access tokenu w minutach.
     * @param refreshTtlDays czas życia refresh tokenu w dniach.
     */
    public JwtService(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_ACCESS_TTL_MINUTES:15}") long accessTtlMinutes,
            @Value("${JWT_REFRESH_TTL_DAYS:7}") long refreshTtlDays
    ) {
        if (secret == null || secret.isBlank() || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);
    }

    /**
     * Generuje access token dla użytkownika.
     *
     * @param userId identyfikator użytkownika jako string (subject).
     * @param username nazwa użytkownika do umieszczenia w claimach.
     * @param role rola użytkownika do umieszczenia w claimach.
     * @return podpisany access token JWT.
     */
    public String generateAccessToken(String userId, String username, String role) {
        return buildToken(userId, Map.of(
                CLAIM_TOKEN_TYPE, "access",
                CLAIM_USERNAME, username,
                CLAIM_ROLE, role
        ), accessTtl);
    }

    /**
     * Generuje refresh token dla użytkownika.
     *
     * @param userId identyfikator użytkownika jako string (subject).
     * @param username nazwa użytkownika do umieszczenia w claimach.
     * @param role rola użytkownika do umieszczenia w claimach.
     * @return podpisany refresh token JWT.
     */
    public String generateRefreshToken(String userId, String username, String role) {
        return buildToken(userId, Map.of(
                CLAIM_TOKEN_TYPE, "refresh",
                CLAIM_USERNAME, username,
                CLAIM_ROLE, role
        ), refreshTtl);
    }

    /**
     * Parsuje token JWT i zwraca wszystkie claimy.
     *
     * @param token token JWT.
     * @return claimy tokenu.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Sprawdza, czy token jest poprawny i podpisany właściwym kluczem.
     *
     * @param token token JWT do walidacji.
     * @return true gdy token jest poprawny, w przeciwnym razie false.
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Zwraca typ tokenu zapisany w claimach.
     *
     * @param token token JWT.
     * @return typ tokenu (np. access/refresh).
     */
    public String getTokenType(String token) {
        return parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }

    /**
     * Zwraca subject tokenu (identyfikator użytkownika).
     *
     * @param token token JWT.
     * @return subject tokenu.
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Zwraca nazwę użytkownika zapisaną w tokenie.
     *
     * @param token token JWT.
     * @return nazwa użytkownika.
     */
    public String getUsername(String token) {
        return parseClaims(token).get(CLAIM_USERNAME, String.class);
    }

    /**
     * Zwraca rolę użytkownika zapisaną w tokenie.
     *
     * @param token token JWT.
     * @return rola użytkownika.
     */
    public String getRole(String token) {
        return parseClaims(token).get(CLAIM_ROLE, String.class);
    }

    /**
     * Zwraca czas życia refresh tokenu.
     *
     * @return czas życia refresh tokenu jako Duration.
     */
    public Duration getRefreshTtl() {
        return refreshTtl;
    }

    /**
     * Buduje token JWT na podstawie danych wejściowych.
     *
     * @param subject subject tokenu (np. identyfikator użytkownika).
     * @param claims dodatkowe claimy do zapisania w tokenie.
     * @param ttl czas życia tokenu.
     * @return podpisany token JWT.
     */
    private String buildToken(String subject, Map<String, Object> claims, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }
}
