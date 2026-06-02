package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO transakcji portfela.
 */
@Data
@AllArgsConstructor
public class TransactionDto {
    /**
     * Konstruktor domyślny.
     */
    public TransactionDto() {}

    private String title;
    private Double amount;
    private String date;
    private boolean isIncome;
}