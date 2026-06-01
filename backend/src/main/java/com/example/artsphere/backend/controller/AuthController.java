package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AccessTokenResponse;
import com.example.artsphere.backend.dto.AuthTokenResponse;
import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.security.JwtService;
import com.example.artsphere.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Kontroler REST do obsługi uwierzytelniania i rejestracji.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint logowania użytkownika.
     * Zwraca dane profilu po poprawnej weryfikacji lub błąd 400 z komunikatem
     * w przypadku niepowodzenia (np. niepoprawne dane lub nieaktywne konto).
     *
     * @param request dane logowania przesłane w ciele żądania (e-mail i hasło).
     * @return odpowiedź HTTP z obiektem {@link AuthTokenResponse} przy sukcesie
     *         albo komunikatem błędu przy niepowodzeniu.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            String accessToken = jwtService.generateAccessToken(
                    response.getUserId().toString(),
                    response.getUsername(),
                    response.getRole()
            );
            String refreshToken = jwtService.generateRefreshToken(
                    response.getUserId().toString(),
                    response.getUsername(),
                    response.getRole()
            );

            ResponseCookie refreshCookie = buildRefreshCookie(refreshToken, jwtService.getRefreshTtl());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(new AuthTokenResponse(accessToken, response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint rejestracji nowego użytkownika.
     * Zwraca tekstowy komunikat potwierdzający utworzenie konta
     * lub błąd 400 z informacją o przyczynie (np. zajęty e-mail).
     *
     * @param request dane rejestracyjne przesłane w ciele żądania.
     * @return odpowiedź HTTP z komunikatem sukcesu lub błędu.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint odświeżający access token na podstawie refresh tokenu z cookie.
     *
     * @param refreshToken refresh token przesłany w cookie.
     * @return nowy access token lub błąd walidacji.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try {
            String accessToken = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(new AccessTokenResponse(accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint wylogowania, który czyści cookie z refresh tokenem.
     *
     * @return odpowiedź HTTP z wyczyszczonym cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = clearRefreshCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Wylogowano");
    }

    /**
     * Buduje cookie HTTP-only dla refresh tokenu.
     *
     * @param refreshToken refresh token do zapisania w cookie.
     * @param ttl czas życia cookie.
     * @return skonfigurowane cookie.
     */
    private ResponseCookie buildRefreshCookie(String refreshToken, Duration ttl) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(ttl)
                .build();
    }

    /**
     * Buduje cookie czyszczące refresh token po wylogowaniu.
     *
     * @return cookie ustawione na wygaśnięcie.
     */
    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ZERO)
                .build();
    }
}