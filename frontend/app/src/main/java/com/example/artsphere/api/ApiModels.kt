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
