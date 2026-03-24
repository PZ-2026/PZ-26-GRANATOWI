package com.example.artsphere.ui

/**
 * Modele danych dla paneli statystyk
 */

// ========== ADMIN STATISTICS ==========
data class AdminStatistics(
    val totalUsers: Int,
    val totalSellers: Int,
    val totalBuyers: Int,
    val totalTransactionValue: Double,
    val averageOrderValue: Double,
    val newUsersThisMonth: Int,
    val totalArtworks: Int,
    val activeListings: Int,
    val completedOrders: Int,
    val pendingOrders: Int
)

// ========== SELLER STATISTICS ==========
data class SellerStatistics(
    val totalSales: Int,
    val totalRevenue: Double,
    val averageRating: Float,
    val followerCount: Int,
    val totalArtworks: Int,
    val activeListings: Int,
    val soldThisMonth: Int,
    val revenueThisMonth: Double,
    val topArtworkTitle: String,
    val topArtworkSales: Int,
    val pendingOrders: Int,
    val completedOrders: Int
)

// ========== CLIENT STATISTICS ==========
data class ClientStatistics(
    val totalSpent: Double,
    val totalPurchases: Int,
    val favoriteArtistsCount: Int,
    val wishlistCount: Int,
    val reviewsGiven: Int,
    val averageRating: Float,
    val spentThisMonth: Double,
    val purchasesThisMonth: Int,
    val memberSince: String,
    val savedArtworks: Int
)

// ========== MOCK DATA PROVIDER ==========
object MockStatisticsProvider {
    
    fun getAdminStatistics(): AdminStatistics {
        return AdminStatistics(
            totalUsers = 1247,
            totalSellers = 156,
            totalBuyers = 1091,
            totalTransactionValue = 284750.50,
            averageOrderValue = 450.25,
            newUsersThisMonth = 87,
            totalArtworks = 3456,
            activeListings = 2341,
            completedOrders = 632,
            pendingOrders = 45
        )
    }
    
    fun getSellerStatistics(): SellerStatistics {
        return SellerStatistics(
            totalSales = 127,
            totalRevenue = 45680.00,
            averageRating = 4.7f,
            followerCount = 234,
            totalArtworks = 52,
            activeListings = 28,
            soldThisMonth = 12,
            revenueThisMonth = 5240.00,
            topArtworkTitle = "Zachód słońca nad jeziorem",
            topArtworkSales = 15,
            pendingOrders = 3,
            completedOrders = 124
        )
    }
    
    fun getClientStatistics(): ClientStatistics {
        return ClientStatistics(
            totalSpent = 8450.00,
            totalPurchases = 23,
            favoriteArtistsCount = 12,
            wishlistCount = 18,
            reviewsGiven = 19,
            averageRating = 4.5f,
            spentThisMonth = 1200.00,
            purchasesThisMonth = 3,
            memberSince = "Styczeń 2024",
            savedArtworks = 34
        )
    }
}
