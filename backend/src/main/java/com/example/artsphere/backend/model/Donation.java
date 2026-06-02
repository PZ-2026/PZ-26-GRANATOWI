package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Encja darowizny.
 */
@Entity
@Table(name = "donations")
@Data
public class Donation {
    /**
     * Konstruktor domyślny.
     */
    public Donation() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    private BigDecimal amount;
}