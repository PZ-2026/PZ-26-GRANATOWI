package com.example.artsphere.api

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Klasa odpowiedzialna za automatyczne odświeżanie tokenu JWT, 
 * gdy serwer zwróci błąd 401 Unauthorized.
 */
class AuthAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Pobieramy zapisany refresh token
        val refreshToken = TokenManager.refreshToken ?: return null

        synchronized(this) {
            // Ponownie sprawdzamy, czy token nie został już odświeżony przez inny wątek
            val currentToken = TokenManager.accessToken
            
            // Próbujemy odświeżyć token na backendzie
            // Tworzymy "czysty" serwis bez tego authenticatora, aby uniknąć pętli
            val newToken = runBlocking {
                try {
                    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                    val cleanClient = OkHttpClient.Builder().addInterceptor(logging).build()
                    val cleanRetrofit = Retrofit.Builder()
                        .baseUrl(RetrofitClient.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(cleanClient)
                        .build()
                    val authService = cleanRetrofit.create(AuthApiService::class.java)

                    val refreshResponse = authService.refresh("refreshToken=$refreshToken")
                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        refreshResponse.body()?.accessToken
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            if (newToken != null) {
                // Zapisujemy nowy token
                TokenManager.accessToken = newToken
                
                // Ponawiamy oryginalne żądanie z nowym tokenem
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                // Jeśli odświeżanie się nie udało, czyścimy dane sesji
                TokenManager.clear()
            }
        }

        // Jeśli nie udało się odświeżyć (np. refresh token wygasł), zwracamy null
        // Spowoduje to ostateczne zwrócenie błędu 401 do aplikacji
        return null
    }
}
