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
}