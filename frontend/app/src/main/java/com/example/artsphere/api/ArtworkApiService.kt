package com.example.artsphere.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ArtworkApiService {

    @Multipart
    @POST("api/artworks/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<Map<String, String>>

    @GET("api/artworks/seller/{userId}")
    suspend fun getSellerArtworks(@Path("userId") userId: Long): Response<List<ArtworkResponse>>

    @GET("api/artworks/categories")
    suspend fun getAllCategories(): Response<List<CategoryResponse>>

    @GET("api/artworks/available")
    suspend fun getAllAvailableArtworks(): Response<List<ArtworkResponse>>

    @GET("api/artworks/{artworkId}")
    suspend fun getArtworkById(@Path("artworkId") artworkId: Long): Response<ArtworkResponse>

    @POST("api/artworks/seller/{userId}")
    suspend fun createArtwork(@Path("userId") userId: Long, @Body request: ArtworkRequest): Response<ArtworkResponse>

    @PUT("api/artworks/{artworkId}/seller/{userId}")
    suspend fun updateArtwork(@Path("artworkId") artworkId: Long, @Path("userId") userId: Long, @Body request: ArtworkRequest): Response<ArtworkResponse>

    @DELETE("api/artworks/{artworkId}/seller/{userId}")
    suspend fun deleteArtwork(@Path("artworkId") artworkId: Long, @Path("userId") userId: Long): Response<String>

    // NOWY ENDPOINT
    @PUT("api/artworks/{artworkId}/mark-sold")
    suspend fun markArtworkAsSold(@Path("artworkId") artworkId: Long): Response<String>
}