package com.example.artsphere.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "artsphere_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "user_role"
    private const val KEY_BALANCE = "user_balance"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var accessToken: String?
        get() = prefs?.getString(KEY_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_TOKEN, value)?.apply()
        }

    var userId: Long
        get() = prefs?.getLong(KEY_USER_ID, 0L) ?: 0L
        set(value) {
            prefs?.edit()?.putLong(KEY_USER_ID, value)?.apply()
        }

    var username: String?
        get() = prefs?.getString(KEY_USERNAME, null)
        set(value) {
            prefs?.edit()?.putString(KEY_USERNAME, value)?.apply()
        }

    var role: String?
        get() = prefs?.getString(KEY_ROLE, "guest")
        set(value) {
            prefs?.edit()?.putString(KEY_ROLE, value)?.apply()
        }

    var balance: Double
        get() = prefs?.getFloat(KEY_BALANCE, 0.0f)?.toDouble() ?: 0.0
        set(value) {
            prefs?.edit()?.putFloat(KEY_BALANCE, value.toFloat())?.apply()
        }

    var isLoggedIn: Boolean
        get() = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
        set(value) {
            prefs?.edit()?.putBoolean(KEY_IS_LOGGED_IN, value)?.apply()
        }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
