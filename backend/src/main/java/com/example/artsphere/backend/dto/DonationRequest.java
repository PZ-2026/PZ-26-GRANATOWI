package com.example.artsphere.backend.dto;
import lombok.Data;

/**
 * DTO żądania darowizny.
 */
@Data
public class DonationRequest {
    /**
     * Konstruktor domyślny.
     */
    public DonationRequest() {}

    private Long clientId;
    private Long sellerId;
    private Double amount;
}