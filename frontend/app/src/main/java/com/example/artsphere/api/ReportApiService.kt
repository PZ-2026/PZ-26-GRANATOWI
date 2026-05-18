package com.example.artsphere.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApiService {

    // ==================== RAPORTY KLIENTA ====================

    @GET("api/reports/client/{userId}/purchases")
    suspend fun getClientPurchaseReport(
        @Path("userId") userId: Long,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String
    ): Response<ResponseBody>

    @GET("api/reports/client/{userId}/transactions")
    suspend fun getClientTransactionsReport(
        @Path("userId") userId: Long,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String
    ): Response<ResponseBody>

    // ==================== RAPORTY SPRZEDAWCY ====================

    @GET("api/reports/seller/{sellerId}/sales")
    suspend fun getSellerSalesReport(
        @Path("sellerId") sellerId: Long,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("category") category: String? = null
    ): Response<ResponseBody>

    // ==================== RAPORTY ADMINA ====================

    @GET("api/reports/admin/user-activity")
    suspend fun getAdminUserActivityReport(
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("role") role: String? = null
    ): Response<ResponseBody>

    @GET("api/reports/admin/commissions")
    suspend fun getAdminCommissionReport(
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("category") category: String? = null
    ): Response<ResponseBody>
}
