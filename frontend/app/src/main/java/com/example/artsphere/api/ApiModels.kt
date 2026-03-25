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
    val message: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val roleName: String
)
