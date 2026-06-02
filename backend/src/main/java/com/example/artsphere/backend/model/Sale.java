package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Encja sprzedaży.
 */
@Entity
@Table(name = "sales")
@Data
public class Sale {
    /**
     * Konstruktor domyślny.
     */
    public Sale() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    @Column(name = "sold_at")
    private LocalDateTime soldAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User buyer;
}