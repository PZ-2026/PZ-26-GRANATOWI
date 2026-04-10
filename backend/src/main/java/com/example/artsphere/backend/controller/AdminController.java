package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AdminUserResponse;
import com.example.artsphere.backend.dto.UpdateUserRoleRequest;
import com.example.artsphere.backend.dto.UpdateUserStatusRequest;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/hash-passwords")
    public Map<String, String> hashAllPasswords() {
        List<User> users = userRepository.findAll();
        int updated = 0;
        
        for (User user : users) {
            // Zahashuj tylko jeśli hasło nie wygląda na hash BCrypt
            if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                String plainPassword = user.getPassword();
                user.setPassword(passwordEncoder.encode(plainPassword));
                userRepository.save(user);
                updated++;
            }
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Zaktualizowano " + updated + " użytkowników");
        result.put("total", String.valueOf(users.size()));
        return result;
    }
    
    @GetMapping("/check-passwords")
    public Map<String, Object> checkPasswords() {
        List<User> users = userRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        
        for (User user : users) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("passwordIsHashed", user.getPassword().startsWith("$2a$") ? "YES" : "NO");
            userInfo.put("passwordPreview", user.getPassword().substring(0, Math.min(30, user.getPassword().length())));
            result.put(user.getUsername(), userInfo);
        }
        
        return result;
    }
    
    @GetMapping("/get-full-hashes")
    public Map<String, String> getFullHashes() {
        List<User> users = userRepository.findAll();
        Map<String, String> result = new HashMap<>();
        
        for (User user : users) {
            result.put(user.getEmail(), user.getPassword());
        }
        
        return result;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> response = userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UpdateUserRoleRequest request
    ) {
        if (request == null || request.getRole() == null || request.getRole().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rola jest wymagana"));
        }

        String normalizedRole = normalizeRole(request.getRole());
        if (!Set.of("ADMIN", "ARTIST", "BUYER").contains(normalizedRole)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nieprawidłowa rola"));
        }

        return userRepository.findById(userId)
                .<ResponseEntity<?>>map(user -> {
                    user.setRole(normalizedRole);
                    User saved = userRepository.save(user);
                    return ResponseEntity.ok(toAdminUserResponse(saved));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nie znaleziono użytkownika")));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateUserStatusRequest request
    ) {
        if (request == null || request.getActive() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status aktywności jest wymagany"));
        }

        return userRepository.findById(userId)
                .<ResponseEntity<?>>map(user -> {
                    user.setIsActive(request.getActive());
                    User saved = userRepository.save(user);
                    return ResponseEntity.ok(toAdminUserResponse(saved));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nie znaleziono użytkownika")));
    }

    @DeleteMapping("/users/{userId}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Nie znaleziono użytkownika"));
        }

        Long id = user.getId();

        jdbcTemplate.update("DELETE FROM seller_user_follows WHERE user_id = ? OR seller_id = ?", id, id);
        jdbcTemplate.update("DELETE FROM donations WHERE client_id = ? OR seller_id = ?", id, id);
        jdbcTemplate.update("DELETE FROM wallet_transactions WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM sales WHERE user_id = ?", id);

        jdbcTemplate.update("DELETE FROM cart_items WHERE artwork_id IN (SELECT id FROM artworks WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM order_items WHERE artwork_id IN (SELECT id FROM artworks WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM sales WHERE artwork_id IN (SELECT id FROM artworks WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM artworks WHERE user_id = ?", id);

        jdbcTemplate.update("DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM carts WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM orders WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM addresses WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM seller_descriptions WHERE user_id = ?", id);

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Użytkownik został usunięty"));
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getBalance() != null ? user.getBalance().doubleValue() : 0.0,
                user.getCreatedAt(),
                !Boolean.FALSE.equals(user.getIsActive())
        );
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase();
        if ("SELLER".equals(normalized)) {
            return "ARTIST";
        }
        return normalized;
    }
}
