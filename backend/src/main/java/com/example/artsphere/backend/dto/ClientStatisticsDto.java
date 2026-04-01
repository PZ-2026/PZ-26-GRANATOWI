package com.example.artsphere.backend.dto;

import lombok.Data;

@Data
public class ClientStatisticsDto {
    private Double totalSpent;
    private Integer totalPurchases;
    private Integer favoriteArtistsCount;
    private Integer wishlistCount;
    private Integer reviewsGiven;
    private Float averageRating;
    private Double spentThisMonth;
    private Integer purchasesThisMonth;
    private String memberSince;
    private Integer savedArtworks;
}