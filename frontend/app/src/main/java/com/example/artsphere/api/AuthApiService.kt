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

    @POST("api/orders/checkout")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ResponseBody>

    @GET("api/orders/user/{userId}/purchases")
    suspend fun getUserPurchases(@Path("userId") userId: Long): Response<List<PurchaseResponse>>

    @DELETE("api/orders/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId: Long): Response<Map<String, String>>

    @POST("api/follows/{userId}/{sellerId}")
    suspend fun followSeller(@Path("userId") userId: Long, @Path("sellerId") sellerId: Long): Response<Map<String, String>>

    @DELETE("api/follows/{userId}/{sellerId}")
    suspend fun unfollowSeller(@Path("userId") userId: Long, @Path("sellerId") sellerId: Long): Response<Map<String, String>>

    @GET("api/follows/{userId}/{sellerId}")
    suspend fun checkFollow(@Path("userId") userId: Long, @Path("sellerId") sellerId: Long): Response<Map<String, Boolean>>

    @GET("api/follows/{userId}/artworks")
    suspend fun getFollowedArtworks(@Path("userId") userId: Long): Response<List<ArtworkResponse>>

    @GET("api/support/artists")
    suspend fun getArtists(): Response<List<ArtistDto>>

    @POST("api/support/donate")
    suspend fun sendSupport(@Body request: DonationRequest): Response<Map<String, Double>>

    @GET("api/support/history/{userId}")
    suspend fun getSupportHistory(@Path("userId") userId: Long): Response<List<DonationHistoryResponse>>

    @GET("api/users/{userId}/statistics/client")
    suspend fun getClientStatistics(@Path("userId") userId: Long): Response<ClientStatisticsDto>

    @GET("api/follows/seller/{sellerId}/followers")
    suspend fun getSellerFollowers(@Path("sellerId") sellerId: Long): Response<List<com.example.artsphere.ui.screens.Seller.Follower>>
}