package com.example.artsphere.api

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String,
    val message: String,
    val balance: Double?
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val roleName: String
)

data class AddressRequest(
    val city: String,
    val postalCode: String,
    val street: String,
    val houseNumber: String,
    val apartmentNumber: String?
)

data class AddressResponse(
    val id: Long,
    val userId: Long,
    val username: String?,
    val city: String?,
    val postalCode: String?,
    val street: String?,
    val houseNumber: String?,
    val apartmentNumber: String?
)

// Artwork DTOs
data class ArtworkRequest(
    val title: String,
    val description: String?,
    val price: Double?,
    val isPriceless: Boolean?,
    val artist: String?,
    val imagePath: String?,
    val width: Double?,
    val height: Double?,
    val depth: Double?,
    val categoryId: Int?
)

data class ArtworkResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val price: Double?,
    val isPriceless: Boolean,
    val artist: String?,
    val imagePath: String?,
    val width: Double?,
    val height: Double?,
    val depth: Double?,
    val userId: Long,
    val userUsername: String,
    val categoryId: Int?,
    val categoryName: String?,
    val isSold: Boolean,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class CategoryResponse(
    val id: Int,
    val name: String
)

data class TransactionResponse(
    val title: String,
    val amount: Double,
    val date: String,
    val income: Boolean
)

data class CreateOrderRequest(
    val userId: Long,
    val totalPrice: Double,
    val artworkIds: List<Long>
)

data class PurchaseResponse(
    val orderId: Long,
    val artworkId: Long,
    val title: String,
    val artist: String,
    val sellerUsername: String,
    val price: Double,
    val date: String
)

data class ArtistDto(
    val id: Long,
    val username: String,
    val firstName: String?,
    val lastName: String?
)

data class DonationRequest(
    val clientId: Long,
    val sellerId: Long,
    val amount: Double
)
data class DonationHistoryResponse(
    val artistName: String,
    val amount: Double
)

data class ClientStatisticsDto(
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

data class SellerStatisticsDto(
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

data class AdminUserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String,
    val balance: Double,
    val createdAt: String?,
    val active: Boolean?,
    val verified: Boolean?
)

data class AdminSellerResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String,
    val createdAt: String?,
    val active: Boolean?,
    val verified: Boolean?,
    val followerCount: Int,
    val totalArtworks: Int,
    val totalRevenue: Double,
    val averageRating: Float
)

data class UpdateUserRoleRequest(
    val role: String
)

data class UpdateUserStatusRequest(
    val active: Boolean
)

data class UpdateUserVerificationRequest(
    val verified: Boolean
)
