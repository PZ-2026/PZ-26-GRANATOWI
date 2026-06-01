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

/**
 * Kontroler REST obsługujący operacje administracyjne.
 */
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

    /**
     * Endpoint administracyjny do jednorazowego haszowania haseł użytkowników,
     * które są jeszcze zapisane w formie jawnej.
     *
     * @return mapa z komunikatem i liczbą wszystkich użytkowników.
     */
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
    
    /**
     * Endpoint zwracający listę wszystkich użytkowników w formacie dla panelu admina.
     *
     * @return lista użytkowników w formacie {@link AdminUserResponse}.
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> response = userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint zwracający listę sprzedawców (ARTIST) w formacie dla panelu admina.
     *
     * @return lista sprzedawców w formacie {@link AdminSellerResponse}.
     */
    @GetMapping("/sellers")
    public ResponseEntity<List<AdminSellerResponse>> getAllSellers() {
        List<AdminSellerResponse> response = userRepository.findAll().stream()
                .filter(u -> "ARTIST".equals(u.getRole()))
                .map(this::toAdminSellerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint aktualizujący rolę użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param request obiekt z nową rolą użytkownika.
     * @return zaktualizowany użytkownik lub komunikat błędu walidacji.
     */
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

    /**
     * Endpoint aktualizujący aktywność konta użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param request obiekt z flagą aktywności.
     * @return zaktualizowany użytkownik lub komunikat błędu.
     */
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

    /**
     * Endpoint ustawiający status weryfikacji sprzedawcy.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param request mapa z flagą "verified".
     * @return zaktualizowany użytkownik/sprzedawca lub błąd walidacji.
     */
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

    /**
     * Endpoint usuwający użytkownika wraz z powiązanymi danymi.
     * Wykonuje ręczne czyszczenie zależności w tabelach.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return komunikat o usunięciu lub błąd, gdy użytkownik nie istnieje.
     */
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
    /**
     * Endpoint administracyjny zwracający wszystkie dzieła.
     *
     * @return lista dzieł w formacie {@link ArtworkResponse}.
     */
    @GetMapping("/artworks")
    public ResponseEntity<List<ArtworkResponse>> getAllArtworks() {
        return ResponseEntity.ok(artworkService.getAllArtworks());
    }

    /**
     * Endpoint administracyjny aktualizujący status dzieła.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @param request mapa z nowym statusem dzieła.
     * @return odpowiedź potwierdzająca aktualizację lub błąd.
     */
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

    /**
     * Endpoint administracyjny usuwający dzieło.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @return odpowiedź potwierdzająca usunięcie lub błąd.
     */
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
    /**
     * Endpoint administracyjny tworzący nową kategorię.
     *
     * @param request mapa z polami kategorii (name, description, parentId).
     * @return odpowiedź z kodem 201 lub błąd walidacji.
     */
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

    /**
     * Endpoint administracyjny aktualizujący status aktywności kategorii.
     *
     * @param categoryId identyfikator kategorii w ścieżce URL.
     * @param request mapa z flagą "isActive".
     * @return odpowiedź potwierdzająca aktualizację lub błąd.
     */
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

    /**
     * Endpoint administracyjny aktualizujący dane kategorii.
     *
     * @param categoryId identyfikator kategorii w ścieżce URL.
     * @param request mapa z polami do aktualizacji (name, description, parentId).
     * @return odpowiedź potwierdzająca aktualizację lub błąd.
     */
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

    /**
     * Endpoint administracyjny odłączający kategorię od nadrzędnej.
     *
     * @param categoryId identyfikator kategorii w ścieżce URL.
     * @return odpowiedź potwierdzająca aktualizację lub błąd.
     */
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

    /**
     * Endpoint administracyjny usuwający kategorię.
     *
     * @param categoryId identyfikator kategorii w ścieżce URL.
     * @return odpowiedź potwierdzająca usunięcie lub błąd.
     */
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

    /**
     * Endpoint zwracający podkategorie dla wskazanej kategorii.
     *
     * @param categoryId identyfikator kategorii nadrzędnej.
     * @return lista podkategorii.
     */
    @GetMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(categoryRepository.findByParentId(categoryId));
    }

    /**
     * Endpoint administracyjny zwracający wszystkie zamówienia
     * zmapowane na format panelu admina.
     *
     * @return lista zamówień w formacie {@link AdminOrderResponse}.
     */
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

    /**
     * Endpoint administracyjny aktualizujący status zamówienia.
     * Obsługuje także anulowanie zamówienia z pełnym skutkiem finansowym.
     *
     * @param orderId identyfikator zamówienia w ścieżce URL.
     * @param request mapa z nowym statusem zamówienia.
     * @return komunikat o wyniku operacji lub błąd walidacji.
     */
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

    /**
     * Endpoint administracyjny anulujący zamówienie.
     *
     * @param orderId identyfikator zamówienia w ścieżce URL.
     * @return komunikat o anulowaniu lub błąd walidacji.
     */
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

    /**
     * Generuje uproszczony slug z nazwy kategorii.
     * Usuwa znaki specjalne i zastępuje spacje myślnikami.
     *
     * @param name nazwa kategorii do znormalizowania.
     * @return slug w formacie przyjaznym dla URL.
     */
    private String generateSlug(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    /**
     * Mapuje zamówienie i pozycję zamówienia na obiekt odpowiedzi dla panelu admina.
     *
     * @param order zamówienie źródłowe.
     * @param item pozycja zamówienia powiązana z dziełem.
     * @param formatter formatter daty używany do prezentacji pól czasowych.
     * @return obiekt {@link AdminOrderResponse} z pełnymi danymi wiersza.
     */
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

    /**
     * Buduje czytelny numer zamówienia w formacie ORD-YYYY-XXXXXX.
     *
     * @param order zamówienie, z którego pobierany jest rok i identyfikator.
     * @return sformatowany numer zamówienia.
     */
    private String formatOrderNumber(Order order) {
        int year = order.getCreatedAt() != null ? order.getCreatedAt().getYear() : LocalDateTime.now().getYear();
        return String.format("ORD-%d-%06d", year, order.getId());
    }

    /**
     * Wyznacza nazwę wyświetlaną użytkownika.
     * Preferuje imię i nazwisko, a gdy brak — nazwę użytkownika.
     *
     * @param user użytkownik, którego nazwa ma zostać wyświetlona.
     * @return tekst do wyświetlenia jako nazwa użytkownika.
     */
    private String getDisplayName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getUsername() : full;
    }

    /**
     * Normalizuje status zamówienia do wartości używanej w panelu.
     *
     * @param status status źródłowy z bazy danych.
     * @return znormalizowany status zamówienia.
     */
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

    /**
     * Wyznacza status płatności na podstawie bieżącego pola i starego statusu zamówienia.
     *
     * @param paymentStatus wartość statusu płatności (może być pusta).
     * @param legacyStatus status zamówienia używany jako fallback.
     * @return znormalizowany status płatności.
     */
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

    /**
     * Składa adres uliczny z ulicy, numeru domu i numeru mieszkania.
     *
     * @param address adres źródłowy.
     * @return sformatowany adres uliczny lub "Brak danych".
     */
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

    /**
     * Zwraca pusty łańcuch znaków, gdy wartość jest null.
     *
     * @param value wartość wejściowa.
     * @return wartość niepusta lub pusty łańcuch.
     */
    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    /**
     * Zastosowuje skutki anulowania zamówienia:
     * zwraca środki kupującemu, koryguje saldo sprzedawcy,
     * aktualizuje dzieła i czyści wpis sprzedaży.
     *
     * @param order zamówienie do anulowania.
     */
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

    /**
     * Dodaje wpis do historii statusów zamówienia.
     *
     * @param order zamówienie, którego historia ma zostać uzupełniona.
     * @param status nowy status zamówienia.
     * @param changedAt czas zmiany statusu (gdy null, ustawiany jest czas bieżący).
     */
    private void appendStatusHistory(Order order, String status, LocalDateTime changedAt) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(changedAt != null ? changedAt : LocalDateTime.now());
        orderStatusHistoryRepository.save(history);
    }

    /**
     * Mapuje użytkownika na obiekt odpowiedzi dla panelu admina.
     *
     * @param user encja użytkownika.
     * @return obiekt {@link AdminUserResponse}.
     */
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

    /**
     * Mapuje sprzedawcę na obiekt odpowiedzi dla panelu admina,
     * uzupełniając statystyki sprzedażowe i liczbę obserwujących.
     *
     * @param user encja sprzedawcy.
     * @return obiekt {@link AdminSellerResponse}.
     */
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

    /**
     * Normalizuje rolę użytkownika, w tym mapuje SELLER na ARTIST.
     *
     * @param role rola podana w żądaniu.
     * @return znormalizowana rola.
     */
    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase();
        if ("SELLER".equals(normalized)) {
            return "ARTIST";
        }
        return normalized;
    }

    /**
     * Endpoint zwracający statystyki na potrzeby panelu administratora.
     *
     * @return statystyki administracyjne lub błąd serwera.
     */
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
