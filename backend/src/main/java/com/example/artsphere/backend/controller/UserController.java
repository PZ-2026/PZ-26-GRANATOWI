package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.dto.TransactionDto;
import com.example.artsphere.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Kontroler REST do obsługi profilu użytkownika i portfela.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    /**
     * Konstruktor domyślny.
     */
    public UserController() {}

    @Autowired
    private UserService userService;

    /**
     * Endpoint zwracający profil użytkownika na podstawie identyfikatora.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return odpowiedź HTTP z profilem użytkownika lub błędem walidacji.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try { return ResponseEntity.ok(userService.getUserProfile(userId)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint aktualizacji profilu użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param updateRequest dane profilu do aktualizacji, w tym opcjonalne hasło.
     * @return odpowiedź HTTP z komunikatem sukcesu lub błędem walidacji.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody RegisterRequest updateRequest) {
        try { return ResponseEntity.ok(Collections.singletonMap("message", userService.updateUserProfile(userId, updateRequest))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    // --- ENDPOINTY PORTFELA ---
    /**
     * Endpoint zasilenia portfela użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param amount kwota do dodania do portfela (parametr zapytania).
     * @return odpowiedź HTTP z nowym saldem lub błędem.
     */
    @PutMapping("/{userId}/balance/add")
    public ResponseEntity<?> addBalance(@PathVariable Long userId, @RequestParam Double amount) {
        try { return ResponseEntity.ok(Collections.singletonMap("newBalance", userService.addBalance(userId, amount))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint obciążenia portfela użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param amount kwota do odjęcia z portfela (parametr zapytania).
     * @return odpowiedź HTTP z nowym saldem lub błędem.
     */
    @PutMapping("/{userId}/balance/deduct")
    public ResponseEntity<?> deductBalance(@PathVariable Long userId, @RequestParam Double amount) {
        try { return ResponseEntity.ok(Collections.singletonMap("newBalance", userService.deductBalance(userId, amount))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint zwracający historię transakcji portfela użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return lista transakcji użytkownika w kolejności od najnowszych.
     */
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }
    /**
     * Endpoint zwracający statystyki klienta (kupującego).
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return statystyki klienta lub błąd w przypadku problemów z wyliczeniem.
     */
    @GetMapping("/{userId}/statistics/client")
    public ResponseEntity<?> getClientStats(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getClientStatistics(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Błąd statystyk"));
        }
    }

    /**
     * Endpoint zwracający statystyki sprzedawcy.
     *
     * @param userId identyfikator sprzedawcy w ścieżce URL.
     * @return statystyki sprzedawcy lub błąd w przypadku problemów z wyliczeniem.
     */
    @GetMapping("/{userId}/statistics/seller")
    public ResponseEntity<?> getSellerStats(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getSellerStatistics(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Błąd statystyk sprzedawcy"));
        }
    }
}