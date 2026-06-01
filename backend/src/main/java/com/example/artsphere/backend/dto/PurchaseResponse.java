package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO odpowiedzi po zakupie.
 */
@Data
@AllArgsConstructor
public class PurchaseResponse {
    private Long orderId;
    private Long artworkId;
    private String title;
    private String artist;
    private String sellerUsername;
    private Double price;
    private String date;
}