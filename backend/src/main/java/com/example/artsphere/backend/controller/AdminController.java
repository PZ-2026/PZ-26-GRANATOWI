package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AdminSellerResponse;
import com.example.artsphere.backend.dto.AdminUserResponse;
import com.example.artsphere.backend.dto.UpdateUserRoleRequest;
import com.example.artsphere.backend.dto.UpdateUserStatusRequest;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.SaleRepository;
import com.example.artsphere.backend.repository.SellerUserFollowRepository;
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
    private ArtworkRepository artworkRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SellerUserFollowRepository followRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/hash-passwords")
    public Map<String, String> hashAllPasswords() {
        List<User> users = userRepository.findAll();
        int updated = 0;
        
        for (User user : users) {
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
    
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> response = userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<AdminSellerResponse>> getAllSellers() {
        List<AdminSellerResponse> response = userRepository.findAll().stream()
                .filter(u -> "ARTIST".equals(u.getRole()))
                .map(this::toAdminSellerResponse)
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

    @PatchMapping("/users/{userId}/verify")
    public ResponseEntity<?> verifySeller(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean verify = request.get("verified");
        if (verify == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status weryfikacji jest wymagany"));
        }

        return userRepository.findById(userId)
                .<ResponseEntity<?>>map(user -> {
                    user.setIsVerified(verify);
                    User saved = userRepository.save(user);
                    if ("ARTIST".equals(saved.getRole())) {
                        return ResponseEntity.ok(toAdminSellerResponse(saved));
                    }
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
                !Boolean.FALSE.equals(user.getIsActive()),
                !Boolean.FALSE.equals(user.getIsVerified())
        );
    }

    private AdminSellerResponse toAdminSellerResponse(User user) {
        int followers = followRepository.findBySellerId(user.getId()).size();
        int artworksCount = artworkRepository.findByUserId(user.getId()).size();
        
        double totalRevenue = saleRepository.findAll().stream()
                .filter(s -> s.getArtwork() != null && s.getArtwork().getUser() != null && s.getArtwork().getUser().getId().equals(user.getId()))
                .mapToDouble(s -> s.getPrice() != null ? s.getPrice().doubleValue() : 0.0)
                .sum();

        return new AdminSellerResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt(),
                !Boolean.FALSE.equals(user.getIsActive()),
                !Boolean.FALSE.equals(user.getIsVerified()),
                followers,
                artworksCount,
                totalRevenue,
                5.0f // Average rating placeholder
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
