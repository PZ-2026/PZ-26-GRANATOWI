package com.example.artsphere.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @GET("api/users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<LoginResponse>

    @PUT("api/users/{userId}")
    suspend fun updateUserProfile(@Path("userId") userId: Long, @Body request: RegisterRequest): Response<Map<String, String>>

    // ENDPOINTY PORTFELA
    @PUT("api/users/{userId}/balance/add")
    suspend fun addBalance(@Path("userId") userId: Long, @Query("amount") amount: Double): Response<Map<String, Double>>

    @PUT("api/users/{userId}/balance/deduct")
    suspend fun deductBalance(@Path("userId") userId: Long, @Query("amount") amount: Double): Response<Map<String, Double>>

    @GET("api/users/{userId}/transactions")
    suspend fun getTransactions(@Path("userId") userId: Long): Response<List<TransactionResponse>>
}