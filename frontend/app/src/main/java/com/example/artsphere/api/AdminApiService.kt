package com.example.artsphere.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.artsphere.api.UpdateCategoryRequest

interface AdminApiService {
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<List<AdminUserResponse>>

    @GET("api/admin/sellers")
    suspend fun getAllSellers(): Response<List<AdminSellerResponse>>

    @GET("api/admin/artworks")
    suspend fun getAllArtworks(): Response<List<ArtworkResponse>>

    @PATCH("api/admin/artworks/{artworkId}/status")
    suspend fun updateArtworkStatus(
        @Path("artworkId") artworkId: Long,
        @Body request: Map<String, String>
    ): Response<Unit>

    @PATCH("api/admin/users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: Long,
        @Body request: UpdateUserRoleRequest
    ): Response<AdminUserResponse>

    @PATCH("api/admin/users/{userId}/status")
    suspend fun updateUserStatus(
        @Path("userId") userId: Long,
        @Body request: UpdateUserStatusRequest
    ): Response<AdminUserResponse>

    @PATCH("api/admin/users/{userId}/verify")
    suspend fun verifySeller(
        @Path("userId") userId: Long,
        @Body request: UpdateUserVerificationRequest
    ): Response<AdminUserResponse>

    @DELETE("api/admin/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Unit>

    @DELETE("api/admin/artworks/{artworkId}")
    suspend fun deleteArtwork(@Path("artworkId") artworkId: Long): Response<Unit>

    // Category endpoints
    @GET("api/artworks/categories")
    suspend fun getAllCategories(): Response<List<CategoryBackendResponse>>

    @POST("api/admin/categories")
    suspend fun createCategory(
        @Body request: UpdateCategoryRequest
    ): Response<Unit>

    @PATCH("api/admin/categories/{categoryId}/status")
    suspend fun updateCategoryStatus(
        @Path("categoryId") categoryId: Long,
        @Body request: Map<String, Boolean>
    ): Response<Unit>

    @PUT("api/admin/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: Long,
        @Body request: UpdateCategoryRequest
    ): Response<Unit>

    @PATCH("api/admin/categories/{categoryId}/detach")
    suspend fun detachCategory(
        @Path("categoryId") categoryId: Long
    ): Response<Unit>

    @DELETE("api/admin/categories/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Long): Response<Unit>

    @GET("api/admin/categories/{categoryId}/subcategories")
    suspend fun getSubcategories(@Path("categoryId") categoryId: Long): Response<List<CategoryBackendResponse>>
}

data class CategoryBackendResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val slug: String?,
    val parentId: Long?,
    val parentName: String?,
    val isActive: Boolean?,
    val artworkCount: Int?,
    val soldArtworkCount: Int?,
    val createdDate: String?,
    val lastModified: String?,
    val displayOrder: Int?,
    val iconName: String?,
    val color: String?
)
