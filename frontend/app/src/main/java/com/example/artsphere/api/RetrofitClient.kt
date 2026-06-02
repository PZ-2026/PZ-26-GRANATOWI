package com.example.artsphere.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    /**
     * Adres bazowy serwera API.
     * Używamy 10.0.2.2 dla emulatora Android Studio (odpowiednik localhost).
     */
    const val BASE_URL = "http://10.0.2.2:8080/"

    /**
     * Interceptor odpowiedzialny za logowanie szczegółów zapytań i odpowiedzi HTTP.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        TokenManager.accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .authenticator(AuthAuthenticator())
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
