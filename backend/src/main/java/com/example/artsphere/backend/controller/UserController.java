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

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try { return ResponseEntity.ok(userService.getUserProfile(userId)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody RegisterRequest updateRequest) {
        try { return ResponseEntity.ok(Collections.singletonMap("message", userService.updateUserProfile(userId, updateRequest))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    // --- ENDPOINTY PORTFELA ---
    @PutMapping("/{userId}/balance/add")
    public ResponseEntity<?> addBalance(@PathVariable Long userId, @RequestParam Double amount) {
        try { return ResponseEntity.ok(Collections.singletonMap("newBalance", userService.addBalance(userId, amount))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @PutMapping("/{userId}/balance/deduct")
    public ResponseEntity<?> deductBalance(@PathVariable Long userId, @RequestParam Double amount) {
        try { return ResponseEntity.ok(Collections.singletonMap("newBalance", userService.deductBalance(userId, amount))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }
    @GetMapping("/{userId}/statistics/client")
    public ResponseEntity<?> getClientStats(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getClientStatistics(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Błąd statystyk"));
        }
    }

    @GetMapping("/{userId}/statistics/seller")
    public ResponseEntity<?> getSellerStats(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getSellerStatistics(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Błąd statystyk sprzedawcy"));
        }
    }
}