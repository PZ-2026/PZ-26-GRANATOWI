package com.example.artsphere.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Zmień na adres IP komputera z backendem, np. "http://192.168.1.105:8080/"
    // Dla emulatora Android Studio użyj: "http://10.0.2.2:8080/"
    // Dla prawdziwego urządzenia użyj IP komputera w tej samej sieci
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)
    val addressApi: AddressApiService = retrofit.create(AddressApiService::class.java)
    val artworkApi: ArtworkApiService = retrofit.create(ArtworkApiService::class.java)
    val adminApi: AdminApiService = retrofit.create(AdminApiService::class.java)
    val reportApi: ReportApiService = retrofit.create(ReportApiService::class.java)
}
