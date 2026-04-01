package com.example.artsphere.backend.dto;
import lombok.Data;

@Data
public class DonationRequest {
    private Long clientId;
    private Long sellerId;
    private Double amount;
}