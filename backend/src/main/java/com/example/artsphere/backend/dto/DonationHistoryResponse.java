package com.example.artsphere.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO pozycji historii darowizn.
 */
@Data @AllArgsConstructor
public class DonationHistoryResponse {
    private String artistName;
    private Double amount;
}