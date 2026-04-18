package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dla statystyk administratora - wyświetlane na panelu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {
    
    // === UŻYTKOWNICY ===
    private int totalUsers;
    private int totalSellers;
    private int totalBuyers;
    private int newUsersThisMonth;
    
    // === TRANSAKCJE ===
    private double totalTransactionValue;
    private double averageOrderValue;
    
    // === DZIEŁA SZTUKI ===
    private int totalArtworks;
    private int activeListings;
    private int soldArtworks;
    
    // === ZAMÓWIENIA ===
    private int pendingOrders;
    private int completedOrders;
    private int totalOrders;
    
    // === DODATKOWE ===
    private double platformRevenue;      // Przychody platformy
    private int totalCategories;
    private double averageUserBalance;
}