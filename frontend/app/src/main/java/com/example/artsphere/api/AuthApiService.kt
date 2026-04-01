package com.example.artsphere.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    // --- NOWE ENDPOINTY DO EDYCJI PROFILU ---
    @GET("api/users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: Long): Response<LoginResponse>

    @PUT("api/users/{userId}")
    suspend fun updateUserProfile(@Path("userId") userId: Long, @Body request: RegisterRequest): Response<Map<String, String>>
}