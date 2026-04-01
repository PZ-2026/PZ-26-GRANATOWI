package com.example.artsphere.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DonationHistoryResponse {
    private String artistName;
    private Double amount;
}