package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AdminSellerResponse;
import com.example.artsphere.backend.dto.AdminUserResponse;
import com.example.artsphere.backend.dto.AdminOrderResponse;
import com.example.artsphere.backend.dto.AdminOrderStatusHistoryResponse;
import com.example.artsphere.backend.dto.ArtworkResponse;
import com.example.artsphere.backend.dto.UpdateUserRoleRequest;
import com.example.artsphere.backend.dto.UpdateUserStatusRequest;
import com.example.artsphere.backend.dto.AdminDashboardStatsDto;
import com.example.artsphere.backend.model.Address;
import com.example.artsphere.backend.model.Artwork;
import com.example.artsphere.backend.model.Category;
import com.example.artsphere.backend.model.Order;
import com.example.artsphere.backend.model.OrderItem;
import com.example.artsphere.backend.model.OrderStatusHistory;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.model.WalletTransaction;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.CategoryRepository;
import com.example.artsphere.backend.repository.OrderItemRepository;
import com.example.artsphere.backend.repository.OrderRepository;
import com.example.artsphere.backend.repository.OrderStatusHistoryRepository;
import com.example.artsphere.backend.repository.SaleRepository;
import com.example.artsphere.backend.repository.SellerUserFollowRepository;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.repository.WalletTransactionRepository;
import com.example.artsphere.backend.service.ArtworkService;
import com.example.artsphere.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private ArtworkService artworkService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

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

    // ARTWORK MANAGEMENT
    @GetMapping("/artworks")
    public ResponseEntity<List<ArtworkResponse>> getAllArtworks() {
        return ResponseEntity.ok(artworkService.getAllArtworks());
    }

    @PatchMapping("/artworks/{artworkId}/status")
    public ResponseEntity<?> updateArtworkStatus(@PathVariable Long artworkId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null) return ResponseEntity.badRequest().body("Status is required");
        
        return artworkRepository.findById(artworkId)
                .map(artwork -> {
                    artwork.setStatus(status);
                    if ("SOLD".equalsIgnoreCase(status)) {
                        artwork.setIsSold(true);
                    } else if ("AVAILABLE".equalsIgnoreCase(status)) {
                        artwork.setIsSold(false);
                    }
                    artworkRepository.save(artwork);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/artworks/{artworkId}")
    public ResponseEntity<?> deleteArtwork(@PathVariable Long artworkId) {
        return artworkRepository.findById(artworkId)
                .map(artwork -> {
                    artworkService.deleteArtwork(artworkId, null); // null bypasses ownership check
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
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

    @GetMapping("/orders")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        List<AdminOrderResponse> response = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .flatMap(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return items.stream().map(item -> toAdminOrderResponse(order, item, formatter));
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/orders/{orderId}/status")
    @Transactional
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status zamówienia jest wymagany"));
        }

        String normalizedStatus = status.trim().toUpperCase();
        if (!Set.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED").contains(normalizedStatus)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nieprawidłowy status zamówienia"));
        }

        return orderRepository.findById(orderId)
                .<ResponseEntity<?>>map(order -> {
                    if ("CANCELLED".equals(order.getStatus()) && !"CANCELLED".equals(normalizedStatus)) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Anulowanego zamówienia nie można ponownie aktywować"));
                    }

                    if (normalizedStatus.equalsIgnoreCase(order.getStatus())) {
                        return ResponseEntity.ok(Map.of("message", "Status zamówienia pozostaje bez zmian"));
                    }

                    if ("CANCELLED".equals(normalizedStatus)) {
                        applyCancellation(order);
                        return ResponseEntity.ok(Map.of("message", "Zamówienie zostało anulowane"));
                    }

                    order.setStatus(normalizedStatus);
                    if (order.getPaymentStatus() == null) {
                        order.setPaymentStatus("PAID");
                    }
                    order.setUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);
                    appendStatusHistory(order, normalizedStatus, order.getUpdatedAt());
                    return ResponseEntity.ok(Map.of("message", "Status zamówienia został zaktualizowany"));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nie znaleziono zamówienia")));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    @Transactional
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .<ResponseEntity<?>>map(order -> {
                    if ("CANCELLED".equals(order.getStatus())) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Zamówienie jest już anulowane"));
                    }
                    applyCancellation(order);
                    return ResponseEntity.ok(Map.of("message", "Zamówienie zostało anulowane"));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nie znaleziono zamówienia")));
    }

    private String generateSlug(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    private AdminOrderResponse toAdminOrderResponse(Order order, OrderItem item, DateTimeFormatter formatter) {
        User buyer = order.getUser();
        User seller = item.getArtwork() != null ? item.getArtwork().getUser() : null;
        Address shippingAddress = order.getShippingAddress();

        String orderStatus = normalizeOrderStatus(order.getStatus());
        String paymentStatus = resolvePaymentStatus(order.getPaymentStatus(), order.getStatus());

        String orderDate = order.getCreatedAt() != null
                ? order.getCreatedAt().format(formatter)
                : "-";

        String actualDelivery = "DELIVERED".equals(orderStatus) && order.getUpdatedAt() != null
                ? order.getUpdatedAt().format(formatter)
                : null;

        Integer quantity = item.getQuantity() != null ? item.getQuantity() : 1;
        double unitPrice = item.getPrice() != null ? item.getPrice().doubleValue() : 0.0;
        double lineTotal = unitPrice * quantity;
        double totalAmount = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : lineTotal;

        List<AdminOrderStatusHistoryResponse> statusHistory = orderStatusHistoryRepository
                .findByOrderIdOrderByChangedAtAsc(order.getId())
                .stream()
                .map(history -> new AdminOrderStatusHistoryResponse(
                        history.getStatus(),
                        history.getChangedAt() != null ? history.getChangedAt().format(formatter) : orderDate
                ))
                .collect(Collectors.toList());

        if (statusHistory.isEmpty()) {
            statusHistory = List.of(new AdminOrderStatusHistoryResponse(orderStatus, orderDate));
        }

        return new AdminOrderResponse(
                order.getId(),
                formatOrderNumber(order),
                buyer != null ? buyer.getId() : null,
                buyer != null ? getDisplayName(buyer) : "Nieznany kupujący",
                buyer != null ? buyer.getEmail() : "-",
                seller != null ? seller.getId() : null,
                seller != null ? getDisplayName(seller) : "Nieznany sprzedawca",
                item.getArtwork() != null ? item.getArtwork().getId() : null,
                item.getArtwork() != null ? item.getArtwork().getTitle() : "Nieznane dzieło",
                item.getArtwork() != null ? item.getArtwork().getImagePath() : null,
                quantity,
                unitPrice,
                totalAmount,
                orderStatus,
                orderDate,
                order.getPaymentMethod() != null ? order.getPaymentMethod() : "Portfel ArtSphere",
                paymentStatus,
                formatStreetAddress(shippingAddress),
                shippingAddress != null ? emptyIfNull(shippingAddress.getCity()) : "Brak danych",
                shippingAddress != null ? emptyIfNull(shippingAddress.getPostalCode()) : "Brak danych",
                "Polska",
                null,
                null,
                actualDelivery,
                null,
                statusHistory
        );
    }

    private String formatOrderNumber(Order order) {
        int year = order.getCreatedAt() != null ? order.getCreatedAt().getYear() : LocalDateTime.now().getYear();
        return String.format("ORD-%d-%06d", year, order.getId());
    }

    private String getDisplayName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getUsername() : full;
    }

    private String normalizeOrderStatus(String status) {
        if (status == null || status.isBlank()) {
            return "PENDING";
        }

        String normalized = status.trim().toUpperCase();
        return switch (normalized) {
            case "PAID" -> "PROCESSING";
            case "COMPLETED" -> "DELIVERED";
            default -> normalized;
        };
    }

    private String resolvePaymentStatus(String paymentStatus, String legacyStatus) {
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            return paymentStatus.trim().toUpperCase();
        }

        if (legacyStatus == null) {
            return "PENDING";
        }

        String normalized = legacyStatus.trim().toUpperCase();
        if ("PAID".equals(normalized)) {
            return "PAID";
        }
        if ("CANCELLED".equals(normalized)) {
            return "REFUNDED";
        }
        return "PENDING";
    }

    private String formatStreetAddress(Address address) {
        if (address == null) {
            return "Brak danych";
        }
        String apartment = address.getApartmentNumber() != null && !address.getApartmentNumber().isBlank()
                ? "/" + address.getApartmentNumber().trim()
                : "";
        return String.format(
                "%s %s%s",
                emptyIfNull(address.getStreet()),
                emptyIfNull(address.getHouseNumber()),
                apartment
        ).trim();
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void applyCancellation(Order order) {
        LocalDateTime now = LocalDateTime.now();
        User buyer = order.getUser();
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        if ("PAID".equals(resolvePaymentStatus(order.getPaymentStatus(), order.getStatus()))
                && buyer != null
                && order.getTotalPrice() != null) {
            BigDecimal buyerBalance = buyer.getBalance() != null ? buyer.getBalance() : BigDecimal.ZERO;
            buyer.setBalance(buyerBalance.add(order.getTotalPrice()));
            userRepository.save(buyer);

            WalletTransaction buyerTx = new WalletTransaction();
            buyerTx.setUser(buyer);
            buyerTx.setTitle("Zwrot środków za anulowanie zamówienia " + formatOrderNumber(order));
            buyerTx.setAmount(order.getTotalPrice());
            buyerTx.setIncome(true);
            walletTransactionRepository.save(buyerTx);
        }

        for (OrderItem item : items) {
            Artwork artwork = item.getArtwork();
            if (artwork == null) {
                continue;
            }

            BigDecimal amount = item.getPrice() != null
                    ? item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1))
                    : BigDecimal.ZERO;

            User seller = artwork.getUser();
            if (seller != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal sellerBalance = seller.getBalance() != null ? seller.getBalance() : BigDecimal.ZERO;
                seller.setBalance(sellerBalance.subtract(amount));
                userRepository.save(seller);

                WalletTransaction sellerTx = new WalletTransaction();
                sellerTx.setUser(seller);
                sellerTx.setTitle("Zwrot po anulowaniu zamówienia " + formatOrderNumber(order) + " (" + artwork.getTitle() + ")");
                sellerTx.setAmount(amount);
                sellerTx.setIncome(false);
                walletTransactionRepository.save(sellerTx);
            }

            artwork.setIsSold(false);
            artwork.setStatus("AVAILABLE");
            artworkRepository.save(artwork);

            if (buyer != null) {
                saleRepository.findTopByArtworkIdAndBuyerIdOrderBySoldAtDesc(artwork.getId(), buyer.getId())
                        .ifPresent(saleRepository::delete);
            }
        }

        order.setStatus("CANCELLED");
        order.setPaymentStatus("PAID".equals(resolvePaymentStatus(order.getPaymentStatus(), order.getStatus())) ? "REFUNDED" : "PENDING");
        order.setUpdatedAt(now);
        orderRepository.save(order);
        appendStatusHistory(order, "CANCELLED", now);
    }

    private void appendStatusHistory(Order order, String status, LocalDateTime changedAt) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(changedAt != null ? changedAt : LocalDateTime.now());
        orderStatusHistoryRepository.save(history);
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

    @GetMapping("/statistics/dashboard")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStatistics() {
        try {
            AdminDashboardStatsDto stats = userService.getAdminDashboardStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
