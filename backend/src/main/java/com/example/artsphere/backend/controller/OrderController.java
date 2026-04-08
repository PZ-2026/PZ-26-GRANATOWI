package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.CreateOrderRequest;
import com.example.artsphere.backend.dto.PurchaseResponse;
import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CreateOrderRequest request) {
        User buyer = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("Brak usera"));

        Order order = new Order();
        order.setUser(buyer);
        order.setTotalPrice(BigDecimal.valueOf(request.getTotalPrice()));
        order.setStatus("PAID");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (Long artId : request.getArtworkIds()) {
            Artwork artwork = artworkRepository.findById(artId).orElse(null);
            if (artwork != null) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setArtwork(artwork);
                item.setPrice(artwork.getPrice());
                item.setQuantity(1);
                orderItemRepository.save(item);

                Sale sale = new Sale();
                sale.setArtwork(artwork);
                sale.setBuyer(buyer);
                sale.setPrice(artwork.getPrice());
                sale.setSoldAt(LocalDateTime.now());
                saleRepository.save(sale);
            }
        }

        return ResponseEntity.ok("Zapisano zamówienie");
    }

    @GetMapping("/user/{userId}/purchases")
    public ResponseEntity<List<PurchaseResponse>> getUserPurchases(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<PurchaseResponse> purchases = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                Artwork art = item.getArtwork();
                purchases.add(new PurchaseResponse(
                        order.getId(),
                        art.getId(),
                        art.getTitle(),
                        art.getArtist(),
                        art.getUser() != null ? art.getUser().getUsername() : "Nieznany",
                        item.getPrice() != null ? item.getPrice().doubleValue() : 0.0,
                        order.getCreatedAt().format(formatter)
                ));
            }
        }

        purchases.sort((p1, p2) -> p2.getOrderId().compareTo(p1.getOrderId()));
        return ResponseEntity.ok(purchases);
    }

    // --- USUWANIE ZAMÓWIENIA Z HISTORII ---
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderRepository.deleteById(orderId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usunięto pomyślnie z historii"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Błąd podczas usuwania"));
        }
    }

    // NAJLEPSI FANI

    @GetMapping("/seller/{sellerId}/top-fans")
    public ResponseEntity<?> getTopFans(@PathVariable Long sellerId) {
        // Pobieramy wszystkie sprzedaże i filtrujemy te dotyczące dzieł danego sprzedawcy
        List<Sale> sellerSales = saleRepository.findAll().stream()
                .filter(s -> s.getArtwork() != null && s.getArtwork().getUser() != null && s.getArtwork().getUser().getId().equals(sellerId))
                .collect(java.util.stream.Collectors.toList());

        // Grupujemy sprzedaże po kupującym
        java.util.Map<User, List<Sale>> salesByBuyer = sellerSales.stream()
                .filter(s -> s.getBuyer() != null)
                .collect(java.util.stream.Collectors.groupingBy(Sale::getBuyer));

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("pl", "PL"));

        // Tworzymy listę wyników i sortujemy po wydanej kwocie
        List<java.util.Map<String, Object>> result = salesByBuyer.entrySet().stream()
                .map(entry -> {
                    User buyer = entry.getKey();
                    List<Sale> purchases = entry.getValue();

                    int count = purchases.size();
                    double totalSpent = purchases.stream()
                            .filter(s -> s.getPrice() != null)
                            .mapToDouble(s -> s.getPrice().doubleValue())
                            .sum();

                    String memberSince = buyer.getCreatedAt() != null ? buyer.getCreatedAt().format(formatter) : "Nieznany";
                    String name = (buyer.getFirstName() != null && !buyer.getFirstName().isEmpty() && buyer.getLastName() != null)
                            ? buyer.getFirstName() + " " + buyer.getLastName()
                            : buyer.getUsername();

                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("name", name);
                    map.put("purchaseCount", count);
                    // Formatowanie na zł z kropką
                    map.put("totalSpent", String.format(java.util.Locale.US, "%.2f zł", totalSpent));
                    map.put("memberSince", memberSince);
                    map.put("rawTotal", totalSpent); // Do sortowania

                    return map;
                })
                .sorted((m1, m2) -> Double.compare((Double) m2.get("rawTotal"), (Double) m1.get("rawTotal"))) // Malejąco wg wydatków
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(result);
    }
}