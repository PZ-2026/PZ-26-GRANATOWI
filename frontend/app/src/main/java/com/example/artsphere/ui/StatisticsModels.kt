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

// ========== ARTWORK MANAGEMENT ==========
data class ArtworkInfo(
    val id: Long,
    val title: String,
    val artist: String?,
    val description: String?,
    val price: Double?,
    val isPriceless: Boolean,
    val category: String?,
    val imagePath: String?,
    val width: Double?,
    val height: Double?,
    val depth: Double?,
    val sellerId: Long,
    val sellerUsername: String,
    val isSold: Boolean,
    val status: String, // "AVAILABLE", "SOLD", "HIDDEN"
    val createdAt: String,
    val views: Int = 0
)

// ========== SELLER MANAGEMENT ==========
data class SellerInfo(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val balance: Double,
    val registrationDate: String,
    val isActive: Boolean = true,
    val lastLogin: String? = null,
    // Statystyki sprzedawcy
    val totalArtworks: Int,
    val soldArtworks: Int,
    val activeArtworks: Int,
    val totalRevenue: Double,
    val followerCount: Int,
    val averageRating: Float,
    val isVerified: Boolean = false
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
    
    fun getMockArtworks(): List<ArtworkInfo> {
        return listOf(
            ArtworkInfo(
                id = 1,
                title = "Zachód słońca nad morzem",
                artist = "Anna Nowak",
                description = "Piękny pejzaż morski przedstawiający zachód słońca. Dzieło wykonane farbami olejnymi na płótnie.",
                price = 1500.00,
                isPriceless = false,
                category = "Malarstwo",
                imagePath = "/images/artworks/sunset.jpg",
                width = 60.0,
                height = 40.0,
                depth = 3.0,
                sellerId = 2,
                sellerUsername = "anna_nowak",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "22.01.2024",
                views = 234
            ),
            ArtworkInfo(
                id = 2,
                title = "Portret kobiety w kapeluszu",
                artist = "Piotr Wiśniewski",
                description = "Ekspresyjny portret wykonany w stylu impresjonistycznym.",
                price = 850.00,
                isPriceless = false,
                category = "Malarstwo",
                imagePath = "/images/artworks/portrait.jpg",
                width = 35.0,
                height = 50.0,
                depth = 2.0,
                sellerId = 3,
                sellerUsername = "piotr_wisniewski",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "05.02.2024",
                views = 187
            ),
            ArtworkInfo(
                id = 3,
                title = "Abstrakcja w błękicie",
                artist = "Katarzyna Wójcik",
                description = "Nowoczesna abstrakcja z dominującymi odcieniami błękitu i złota.",
                price = null,
                isPriceless = true,
                category = "Sztuka współczesna",
                imagePath = "/images/artworks/abstract.jpg",
                width = 80.0,
                height = 60.0,
                depth = 4.0,
                sellerId = 6,
                sellerUsername = "katarzyna_wojcik",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "25.02.2024",
                views = 456
            ),
            ArtworkInfo(
                id = 4,
                title = "Miejski pejzaż nocą",
                artist = "Magdalena Zielińska",
                description = "Nocny widok na miasto z lotu ptaka. Gra świateł i cieni.",
                price = 1800.00,
                isPriceless = false,
                category = "Fotografia",
                imagePath = "/images/artworks/citynight.jpg",
                width = 70.0,
                height = 50.0,
                depth = 1.0,
                sellerId = 8,
                sellerUsername = "magdalena_zielinska",
                isSold = true,
                status = "SOLD",
                createdAt = "08.03.2024",
                views = 312
            ),
            ArtworkInfo(
                id = 5,
                title = "Rzeźba \"Harmonia\"",
                artist = "Marek Kozłowski",
                description = "Współczesna rzeźba z brązu przedstawiająca zrównoważone formy.",
                price = 4500.00,
                isPriceless = false,
                category = "Rzeźba",
                imagePath = "/images/artworks/sculpture.jpg",
                width = 30.0,
                height = 45.0,
                depth = 30.0,
                sellerId = 11,
                sellerUsername = "marek_kozlowski",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "22.03.2024",
                views = 89
            ),
            ArtworkInfo(
                id = 6,
                title = "Kwiaty w wazonie",
                artist = "Anna Nowak",
                description = "Martwa natura z bukietem polnych kwiatów.",
                price = 650.00,
                isPriceless = false,
                category = "Malarstwo",
                imagePath = "/images/artworks/flowers.jpg",
                width = 40.0,
                height = 50.0,
                depth = 2.0,
                sellerId = 2,
                sellerUsername = "anna_nowak",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "10.02.2024",
                views = 156
            ),
            ArtworkInfo(
                id = 7,
                title = "Górski krajobraz",
                artist = "Piotr Wiśniewski",
                description = "Widok na Tatry o wschodzie słońca.",
                price = 2200.00,
                isPriceless = false,
                category = "Malarstwo",
                imagePath = "/images/artworks/mountains.jpg",
                width = 90.0,
                height = 60.0,
                depth = 3.0,
                sellerId = 3,
                sellerUsername = "piotr_wisniewski",
                isSold = true,
                status = "SOLD",
                createdAt = "15.02.2024",
                views = 567
            ),
            ArtworkInfo(
                id = 8,
                title = "Minimalizm #3",
                artist = "Katarzyna Wójcik",
                description = "Minimalistyczna kompozycja geometryczna.",
                price = 980.00,
                isPriceless = false,
                category = "Sztuka współczesna",
                imagePath = "/images/artworks/minimal.jpg",
                width = 50.0,
                height = 50.0,
                depth = 2.0,
                sellerId = 6,
                sellerUsername = "katarzyna_wojcik",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "01.03.2024",
                views = 201
            ),
            ArtworkInfo(
                id = 9,
                title = "Portret starej kobiety",
                artist = "Magdalena Zielińska",
                description = "Czarno-biała fotografia portretowa.",
                price = 450.00,
                isPriceless = false,
                category = "Fotografia",
                imagePath = "/images/artworks/oldwoman.jpg",
                width = 30.0,
                height = 40.0,
                depth = 1.0,
                sellerId = 8,
                sellerUsername = "magdalena_zielinska",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "12.03.2024",
                views = 128
            ),
            ArtworkInfo(
                id = 10,
                title = "Taniec formy",
                artist = "Marek Kozłowski",
                description = "Dynamiczna rzeźba przedstawiająca ruch.",
                price = null,
                isPriceless = true,
                category = "Rzeźba",
                imagePath = "/images/artworks/dance.jpg",
                width = 25.0,
                height = 60.0,
                depth = 25.0,
                sellerId = 11,
                sellerUsername = "marek_kozlowski",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "25.03.2024",
                views = 67
            ),
            ArtworkInfo(
                id = 11,
                title = "Jesienne liście",
                artist = "Anna Nowak",
                description = "Kolorowa kompozycja z jesiennych liści.",
                price = 720.00,
                isPriceless = false,
                category = "Malarstwo",
                imagePath = "/images/artworks/autumn.jpg",
                width = 45.0,
                height = 35.0,
                depth = 2.0,
                sellerId = 2,
                sellerUsername = "anna_nowak",
                isSold = false,
                status = "HIDDEN",
                createdAt = "05.03.2024",
                views = 43
            ),
            ArtworkInfo(
                id = 12,
                title = "Architektura brutalna",
                artist = "Piotr Wiśniewski",
                description = "Fotografia modernistycznej architektury.",
                price = 890.00,
                isPriceless = false,
                category = "Fotografia",
                imagePath = "/images/artworks/architecture.jpg",
                width = 60.0,
                height = 40.0,
                depth = 1.0,
                sellerId = 3,
                sellerUsername = "piotr_wisniewski",
                isSold = false,
                status = "AVAILABLE",
                createdAt = "18.03.2024",
                views = 178
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
    
    fun getMockSellers(): List<SellerInfo> {
        return listOf(
            SellerInfo(
                id = 2,
                username = "anna_nowak",
                email = "anna.nowak@example.com",
                firstName = "Anna",
                lastName = "Nowak",
                balance = 15680.50,
                registrationDate = "22.01.2024",
                isActive = true,
                lastLogin = "27.03.2026 14:30",
                totalArtworks = 8,
                soldArtworks = 4,
                activeArtworks = 4,
                totalRevenue = 6370.00,
                followerCount = 87,
                averageRating = 4.7f,
                isVerified = true
            ),
            SellerInfo(
                id = 3,
                username = "piotr_wisniewski",
                email = "piotr.wisniewski@example.com",
                firstName = "Piotr",
                lastName = "Wiśniewski",
                balance = 8420.00,
                registrationDate = "05.02.2024",
                isActive = true,
                lastLogin = "26.03.2026 20:45",
                totalArtworks = 5,
                soldArtworks = 2,
                activeArtworks = 3,
                totalRevenue = 4850.00,
                followerCount = 45,
                averageRating = 4.3f,
                isVerified = true
            ),
            SellerInfo(
                id = 6,
                username = "katarzyna_wojcik",
                email = "k.wojcik@example.com",
                firstName = "Katarzyna",
                lastName = "Wójcik",
                balance = 22100.00,
                registrationDate = "25.02.2024",
                isActive = true,
                lastLogin = "27.03.2026 17:00",
                totalArtworks = 12,
                soldArtworks = 5,
                activeArtworks = 7,
                totalRevenue = 12450.00,
                followerCount = 156,
                averageRating = 4.9f,
                isVerified = true
            ),
            SellerInfo(
                id = 8,
                username = "magdalena_zielinska",
                email = "m.zielinska@example.com",
                firstName = "Magdalena",
                lastName = "Zielińska",
                balance = 11250.00,
                registrationDate = "08.03.2024",
                isActive = true,
                lastLogin = "27.03.2026 09:45",
                totalArtworks = 6,
                soldArtworks = 3,
                activeArtworks = 3,
                totalRevenue = 4050.00,
                followerCount = 62,
                averageRating = 4.5f,
                isVerified = true
            ),
            SellerInfo(
                id = 11,
                username = "marek_kozlowski",
                email = "marek.kozlowski@example.com",
                firstName = "Marek",
                lastName = "Kozłowski",
                balance = 18900.00,
                registrationDate = "22.03.2024",
                isActive = true,
                lastLogin = "27.03.2026 15:50",
                totalArtworks = 4,
                soldArtworks = 1,
                activeArtworks = 3,
                totalRevenue = 7500.00,
                followerCount = 34,
                averageRating = 4.8f,
                isVerified = false
            ),
            SellerInfo(
                id = 13,
                username = "jan_kowalczyk",
                email = "jan.kowal@example.com",
                firstName = "Jan",
                lastName = "Kowalczyk",
                balance = 3200.00,
                registrationDate = "10.03.2024",
                isActive = false,
                lastLogin = "12.03.2026 11:20",
                totalArtworks = 2,
                soldArtworks = 0,
                activeArtworks = 2,
                totalRevenue = 0.00,
                followerCount = 8,
                averageRating = 0.0f,
                isVerified = false
            )
        )
    }
}
