package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO statystyk sprzedawcy.
 */
@Data
public class SellerStatisticsDto {
    private Integer totalSales;
    private Double totalRevenue;
    private Float averageRating;
    private Integer followerCount;
    private Integer totalArtworks;
    private Integer activeListings;
    private Integer soldThisMonth;
    private Double revenueThisMonth;
    private String topArtworkTitle;
    private Integer topArtworkSales; // w obecnym modelu można dać 1 (lub ilość, jeśli planujesz zmianę systemu)
    private Integer pendingOrders;
    private Integer completedOrders;
}