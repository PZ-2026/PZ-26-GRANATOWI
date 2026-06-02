package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Encja pozycji zamówienia.
 */
@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    /**
     * Konstruktor domyślny.
     */
    public OrderItem() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    private Integer quantity = 1;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}