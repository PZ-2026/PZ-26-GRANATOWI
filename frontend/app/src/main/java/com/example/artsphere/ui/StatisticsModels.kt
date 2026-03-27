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

// ========== USER MANAGEMENT ==========
data class UserInfo(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String, // "BUYER", "SELLER", "ADMIN"
    val balance: Double,
    val registrationDate: String,
    val isActive: Boolean = true,
    val lastLogin: String? = null
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
    
    fun getMockUsers(): List<UserInfo> {
        return listOf(
            UserInfo(
                id = 1,
                username = "jankowalski",
                email = "jan.kowalski@example.com",
                firstName = "Jan",
                lastName = "Kowalski",
                role = "BUYER",
                balance = 2500.00,
                registrationDate = "15.01.2024",
                isActive = true,
                lastLogin = "27.03.2026 18:15"
            ),
            UserInfo(
                id = 2,
                username = "anna_nowak",
                email = "anna.nowak@example.com",
                firstName = "Anna",
                lastName = "Nowak",
                role = "SELLER",
                balance = 15680.50,
                registrationDate = "22.01.2024",
                isActive = true,
                lastLogin = "27.03.2026 14:30"
            ),
            UserInfo(
                id = 3,
                username = "piotr_wisniewski",
                email = "piotr.wisniewski@example.com",
                firstName = "Piotr",
                lastName = "Wiśniewski",
                role = "SELLER",
                balance = 8420.00,
                registrationDate = "05.02.2024",
                isActive = true,
                lastLogin = "26.03.2026 20:45"
            ),
            UserInfo(
                id = 4,
                username = "maria_kowalczyk",
                email = "maria.kowalczyk@example.com",
                firstName = "Maria",
                lastName = "Kowalczyk",
                role = "BUYER",
                balance = 1200.00,
                registrationDate = "10.02.2024",
                isActive = true,
                lastLogin = "27.03.2026 10:20"
            ),
            UserInfo(
                id = 5,
                username = "tomasz_lewandowski",
                email = "tomasz.lew@example.com",
                firstName = "Tomasz",
                lastName = "Lewandowski",
                role = "BUYER",
                balance = 3450.00,
                registrationDate = "18.02.2024",
                isActive = false,
                lastLogin = "15.03.2026 16:30"
            ),
            UserInfo(
                id = 6,
                username = "katarzyna_wojcik",
                email = "k.wojcik@example.com",
                firstName = "Katarzyna",
                lastName = "Wójcik",
                role = "SELLER",
                balance = 22100.00,
                registrationDate = "25.02.2024",
                isActive = true,
                lastLogin = "27.03.2026 17:00"
            ),
            UserInfo(
                id = 7,
                username = "andrzej_kaminski",
                email = "andrzej.k@example.com",
                firstName = "Andrzej",
                lastName = "Kamiński",
                role = "BUYER",
                balance = 890.00,
                registrationDate = "03.03.2024",
                isActive = true,
                lastLogin = "25.03.2026 12:15"
            ),
            UserInfo(
                id = 8,
                username = "magdalena_zielinska",
                email = "m.zielinska@example.com",
                firstName = "Magdalena",
                lastName = "Zielińska",
                role = "SELLER",
                balance = 11250.00,
                registrationDate = "08.03.2024",
                isActive = true,
                lastLogin = "27.03.2026 09:45"
            ),
            UserInfo(
                id = 9,
                username = "krzysztof_szymanski",
                email = "k.szymanski@example.com",
                firstName = "Krzysztof",
                lastName = "Szymański",
                role = "BUYER",
                balance = 4200.00,
                registrationDate = "15.03.2024",
                isActive = true,
                lastLogin = "27.03.2026 11:30"
            ),
            UserInfo(
                id = 10,
                username = "joanna_wozniak",
                email = "joanna.w@example.com",
                firstName = "Joanna",
                lastName = "Woźniak",
                role = "BUYER",
                balance = 1850.00,
                registrationDate = "20.03.2024",
                isActive = true,
                lastLogin = "26.03.2026 19:20"
            ),
            UserInfo(
                id = 11,
                username = "marek_kozlowski",
                email = "marek.kozlowski@example.com",
                firstName = "Marek",
                lastName = "Kozłowski",
                role = "SELLER",
                balance = 18900.00,
                registrationDate = "22.03.2024",
                isActive = true,
                lastLogin = "27.03.2026 15:50"
            ),
            UserInfo(
                id = 12,
                username = "ewa_jankowska",
                email = "ewa.jankowska@example.com",
                firstName = "Ewa",
                lastName = "Jankowska",
                role = "ADMIN",
                balance = 0.00,
                registrationDate = "01.01.2024",
                isActive = true,
                lastLogin = "27.03.2026 18:00"
            )
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
