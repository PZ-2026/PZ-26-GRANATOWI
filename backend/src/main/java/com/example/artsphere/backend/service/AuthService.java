package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.logging.AuthFileLogger;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Serwis odpowiedzialny za uwierzytelnianie i rejestrację użytkowników.
 */
@Service
public class AuthService {
    /**
     * Konstruktor domyślny.
     */
    public AuthService() {}

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthFileLogger authFileLogger;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Przeprowadza logowanie użytkownika na podstawie danych z formularza logowania.
     * Weryfikuje istnienie konta, aktywność oraz poprawność hasła, a następnie
     * zwraca dane profilu do użycia po stronie klienta.
     *
     * @param request obiekt z danymi logowania; oczekiwane są poprawne wartości
     *                e-mail oraz hasła wprost z formularza (bez wstępnej walidacji).
     * @return odpowiedź logowania zawierająca identyfikator, dane profilu, rolę,
     *         komunikat oraz aktualne saldo użytkownika.
     * @throws RuntimeException gdy użytkownik nie istnieje, konto jest nieaktywne
     *                          lub hasło nie pasuje do zapisanego skrótu.
     */
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            authFileLogger.logLogin(request.getEmail(), false, "Nieprawidłowy e-mail lub hasło");
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        User user = userOpt.get();

        if (Boolean.FALSE.equals(user.getIsActive())) {
            authFileLogger.logLogin(user.getEmail(), false, "Konto jest nieaktywne");
            throw new RuntimeException("Konto jest nieaktywne");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            authFileLogger.logLogin(request.getEmail(), false, "Nieprawidłowy e-mail lub hasło");
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        String roleName = user.getRole() != null ? user.getRole() : "BUYER";
        BigDecimal userBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        authFileLogger.logLogin(user.getEmail(), true, "Zalogowano pomyślnie");

        return new LoginResponse(
                user.getId(),
                safe(user.getUsername()),
                safe(user.getEmail()),
                safe(user.getFirstName()),
                safe(user.getLastName()),
                roleName,
                "Zalogowano pomyślnie",
                userBalance
        );
    }

    /**
     * Rejestruje nowego użytkownika i zapisuje go w bazie danych.
     * Sprawdza unikalność nazwy użytkownika i adresu e-mail, koduje hasło
     * algorytmem BCrypt i ustawia rolę domyślną BUYER, jeśli rola nie została podana.
     *
     * @param request dane rejestracji użytkownika; wymagane są przynajmniej
     *                username, email i password, opcjonalnie firstName, lastName oraz roleName.
     * @return komunikat potwierdzający pomyślną rejestrację.
     * @throws RuntimeException gdy nazwa użytkownika lub adres e-mail są już zajęte.
     */
    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            authFileLogger.logRegister(request.getUsername(), request.getEmail(), false, "Użytkownik o takiej nazwie już istnieje");
            throw new RuntimeException("Użytkownik o takiej nazwie już istnieje");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            authFileLogger.logRegister(request.getUsername(), request.getEmail(), false, "Użytkownik o takim e-mail już istnieje");
            throw new RuntimeException("Użytkownik o takim e-mail już istnieje");
        }

        User user = new User();
        user.setUsername(safe(request.getUsername()));
        user.setEmail(safe(request.getEmail()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(safe(request.getFirstName()));
        user.setLastName(safe(request.getLastName()));
        user.setBalance(BigDecimal.ZERO);

        String roleName = request.getRoleName() != null ? request.getRoleName() : "BUYER";
        user.setRole(roleName);

        userRepository.save(user);
        authFileLogger.logRegister(request.getUsername(), request.getEmail(), true, "Zarejestrowano pomyślnie");
        return "Zarejestrowano pomyślnie";
    }

    /**
     * Odświeża access token na podstawie refresh tokenu.
     * Weryfikuje typ tokenu, a następnie sprawdza, czy użytkownik istnieje
     * i czy jego konto jest aktywne.
     *
     * @param refreshToken refresh token JWT przesłany przez klienta.
     * @return nowy access token JWT.
     * @throws RuntimeException gdy token jest nieprawidłowy, użytkownik nie istnieje
     *                          lub konto jest nieaktywne.
     */
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtService.isValid(refreshToken)) {
            throw new RuntimeException("Nieprawidłowy refresh token");
        }
        if (!"refresh".equals(jwtService.getTokenType(refreshToken))) {
            throw new RuntimeException("Nieprawidłowy typ tokenu");
        }

        String userId = jwtService.getSubject(refreshToken);
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Konto jest nieaktywne");
        }

        String roleName = user.getRole() != null ? user.getRole() : "BUYER";
        return jwtService.generateAccessToken(user.getId().toString(), user.getUsername(), roleName);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
