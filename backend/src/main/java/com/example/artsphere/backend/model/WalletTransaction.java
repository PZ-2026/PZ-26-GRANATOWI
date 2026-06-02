package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Encja transakcji portfela.
 */
@Entity
@Table(name = "wallet_transactions")
@Data
public class WalletTransaction {
    /**
     * Konstruktor domyślny.
     */
    public WalletTransaction() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "is_income", nullable = false)
    private boolean isIncome;
}