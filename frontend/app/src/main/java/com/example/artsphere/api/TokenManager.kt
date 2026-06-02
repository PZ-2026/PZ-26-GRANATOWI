package com.example.artsphere.api

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager odpowiedzialny za trwałe przechowywanie danych sesji użytkownika.
 * Wykorzystuje SharedPreferences do zapisywania tokenów JWT oraz podstawowych informacji o profilu.
 */
object TokenManager {
    private const val PREFS_NAME = "artsphere_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "user_role"
    private const val KEY_BALANCE = "user_balance"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private var prefs: SharedPreferences? = null

    /**
     * Inicjalizuje managera przy użyciu kontekstu aplikacji.
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Przechowuje aktualny Access Token JWT.
     */
    var accessToken: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_TOKEN, value)?.apply()
        }

    /**
     * Przechowuje aktualny Refresh Token JWT.
     */
    var refreshToken: String?
        get() = prefs?.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_REFRESH_TOKEN, value)?.apply()
        }

    /**
     * Przechowuje unikalny identyfikator zalogowanego użytkownika.
     */
    var userId: Long
        get() = prefs?.getLong(KEY_USER_ID, 0L) ?: 0L
        set(value) {
            prefs?.edit()?.putLong(KEY_USER_ID, value)?.apply()
        }

    /**
     * Przechowuje nazwę wyświetlaną użytkownika.
     */
    var username: String?
        get() = prefs?.getString(KEY_USERNAME, null)
        set(value) {
            prefs?.edit()?.putString(KEY_USERNAME, value)?.apply()
        }

    /**
     * Przechowuje rolę użytkownika.
     */
    var role: String?
        get() = prefs?.getString(KEY_ROLE, "guest")
        set(value) {
            prefs?.edit()?.putString(KEY_ROLE, value)?.apply()
        }

    /**
     * Przechowuje aktualne saldo portfela użytkownika.
     */
    var balance: Double
        get() = prefs?.getFloat(KEY_BALANCE, 0.0f)?.toDouble() ?: 0.0
        set(value) {
            prefs?.edit()?.putFloat(KEY_BALANCE, value.toFloat())?.apply()
        }

    /**
     * Flaga informująca, czy użytkownik jest obecnie zalogowany.
     */
    var isLoggedIn: Boolean
        get() = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
        set(value) {
            prefs?.edit()?.putBoolean(KEY_IS_LOGGED_IN, value)?.apply()
        }

    /**
     * Usuwa wszystkie dane sesji z pamięci trwałej (wylogowanie).
     */
    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
