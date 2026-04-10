package com.example.artsphere.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AdminApiService {
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<List<AdminUserResponse>>

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

    @DELETE("api/admin/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Unit>
}
