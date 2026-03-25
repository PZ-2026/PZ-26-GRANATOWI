package com.example.artsphere.api

import retrofit2.Response
import retrofit2.http.*

interface AddressApiService {
    
    @GET("api/addresses/user/{userId}")
    suspend fun getUserAddresses(@Path("userId") userId: Long): Response<List<AddressResponse>>
    
    @POST("api/addresses/user/{userId}")
    suspend fun createAddress(
        @Path("userId") userId: Long,
        @Body request: AddressRequest
    ): Response<AddressResponse>
    
    @PUT("api/addresses/{addressId}/user/{userId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: Long,
        @Path("userId") userId: Long,
        @Body request: AddressRequest
    ): Response<AddressResponse>
    
    @DELETE("api/addresses/{addressId}/user/{userId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: Long,
        @Path("userId") userId: Long
    ): Response<String>
    
    @GET("api/addresses/admin/all")
    suspend fun getAllAddresses(): Response<List<AddressResponse>>

    @GET("api/addresses/admin/{addressId}")
    suspend fun getAddressById(@Path("addressId") addressId: Long): Response<AddressResponse>

    @PUT("api/addresses/admin/{addressId}")
    suspend fun adminUpdateAddress(
        @Path("addressId") addressId: Long,
        @Body request: AddressRequest
    ): Response<AddressResponse>

    @DELETE("api/addresses/admin/{addressId}")
    suspend fun adminDeleteAddress(@Path("addressId") addressId: Long): Response<String>
}
