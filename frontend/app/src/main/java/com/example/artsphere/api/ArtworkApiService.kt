package com.example.artsphere.api

import retrofit2.Response
import retrofit2.http.*

interface ArtworkApiService {
    
    @GET("api/artworks/seller/{userId}")
    suspend fun getSellerArtworks(@Path("userId") userId: Long): Response<List<ArtworkResponse>>
    
    @GET("api/artworks/categories")
    suspend fun getAllCategories(): Response<List<CategoryResponse>>
    
    @GET("api/artworks/available")
    suspend fun getAllAvailableArtworks(): Response<List<ArtworkResponse>>
    
    @GET("api/artworks/{artworkId}")
    suspend fun getArtworkById(@Path("artworkId") artworkId: Long): Response<ArtworkResponse>
    
    @POST("api/artworks/seller/{userId}")
    suspend fun createArtwork(
        @Path("userId") userId: Long,
        @Body request: ArtworkRequest
    ): Response<ArtworkResponse>
    
    @PUT("api/artworks/{artworkId}/seller/{userId}")
    suspend fun updateArtwork(
        @Path("artworkId") artworkId: Long,
        @Path("userId") userId: Long,
        @Body request: ArtworkRequest
    ): Response<ArtworkResponse>
    
    @DELETE("api/artworks/{artworkId}/seller/{userId}")
    suspend fun deleteArtwork(
        @Path("artworkId") artworkId: Long,
        @Path("userId") userId: Long
    ): Response<String>
}