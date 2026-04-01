package com.example.artsphere.api

import retrofit2.Response
import retrofit2.http.*

interface AddressApiService {
    @GET("api/addresses/user/{userId}")
    suspend fun getUserAddresses(@Path("userId") userId: Long): Response<List<AddressResponse>>

    @GET("api/addresses/{id}")
    suspend fun getAddressById(@Path("id") id: Long): Response<AddressResponse>

    @POST("api/addresses/user/{userId}")
    suspend fun addAddress(@Path("userId") userId: Long, @Body request: AddressRequest): Response<AddressResponse>

    @PUT("api/addresses/{id}/user/{userId}")
    suspend fun updateAddress(@Path("id") id: Long, @Path("userId") userId: Long, @Body request: AddressRequest): Response<AddressResponse>

    @DELETE("api/addresses/{id}/user/{userId}")
    suspend fun deleteAddress(@Path("id") id: Long, @Path("userId") userId: Long): Response<Map<String, String>>

    @GET("api/addresses/admin/all")
    suspend fun getAllAddresses(): Response<List<AddressResponse>>

    @PUT("api/addresses/admin/{id}")
    suspend fun adminUpdateAddress(@Path("id") id: Long, @Body request: AddressRequest): Response<AddressResponse>

    @DELETE("api/addresses/admin/{id}")
    suspend fun adminDeleteAddress(@Path("id") id: Long): Response<Map<String, String>>
}