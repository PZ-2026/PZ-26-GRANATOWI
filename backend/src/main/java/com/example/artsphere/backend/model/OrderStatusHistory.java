package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Encja historii statusów zamówienia.
 */
@Entity
@Table(name = "order_status_history")
@Data
public class OrderStatusHistory {
    /**
     * Konstruktor domyślny.
     */
    public OrderStatusHistory() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}
