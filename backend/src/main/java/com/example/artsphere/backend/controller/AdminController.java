package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AdminSellerResponse;
import com.example.artsphere.backend.dto.AdminUserResponse;
import com.example.artsphere.backend.dto.UpdateUserRoleRequest;
import com.example.artsphere.backend.dto.UpdateUserStatusRequest;
import com.example.artsphere.backend.model.Category;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.CategoryRepository;
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

import java.time.LocalDateTime;
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
    private CategoryRepository categoryRepository;

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

    // CATEGORY MANAGEMENT
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> request) {
        try {
            Category category = new Category();
            category.setName((String) request.get("name"));
            category.setDescription((String) request.get("description"));
            category.setSlug(generateSlug((String) request.get("name")));
            category.setIsActive(true);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            category.setDisplayOrder(0);

            if (request.get("parentId") != null) {
                Integer parentId = Integer.valueOf(request.get("parentId").toString());
                category.setParent(categoryRepository.findById(parentId).orElse(null));
            }

            categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/categories/{categoryId}/status")
    public ResponseEntity<?> updateCategoryStatus(
            @PathVariable Integer categoryId,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean isActive = request.get("isActive");
        if (isActive == null) return ResponseEntity.badRequest().body("Status is required");

        return categoryRepository.findById(categoryId)
                .map(cat -> {
                    cat.setIsActive(isActive);
                    cat.setUpdatedAt(LocalDateTime.now());
                    categoryRepository.save(cat);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody Map<String, Object> request
    ) {
        return categoryRepository.findById(categoryId)
                .map(cat -> {
                    if (request.containsKey("name")) {
                        String name = (String) request.get("name");
                        cat.setName(name);
                        cat.setSlug(generateSlug(name));
                    }
                    if (request.containsKey("description")) {
                        cat.setDescription((String) request.get("description"));
                    }
                    if (request.containsKey("parentId")) {
                        Object pId = request.get("parentId");
                        if (pId == null) {
                            cat.setParent(null);
                        } else {
                            Integer parentId = Integer.valueOf(pId.toString());
                            cat.setParent(categoryRepository.findById(parentId).orElse(null));
                        }
                    }
                    cat.setUpdatedAt(LocalDateTime.now());
                    categoryRepository.save(cat);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/categories/{categoryId}/detach")
    public ResponseEntity<?> detachCategory(@PathVariable Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(cat -> {
                    cat.setParent(null);
                    cat.setUpdatedAt(LocalDateTime.now());
                    categoryRepository.save(cat);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) return ResponseEntity.notFound().build();
        try {
            categoryRepository.deleteById(categoryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Nie można usunąć kategorii. Upewnij się, że nie ma przypisanych dzieł.");
        }
    }

    @GetMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(categoryRepository.findByParentId(categoryId));
    }

    private String generateSlug(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
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