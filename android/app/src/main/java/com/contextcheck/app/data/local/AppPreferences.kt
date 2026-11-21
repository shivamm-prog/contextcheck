package com.contextcheck.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.contextcheck.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("contextcheck_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }

    private val _baseUrlFlow = MutableStateFlow(getBaseUrl())
    val baseUrlFlow: StateFlow<String> = _baseUrlFlow.asStateFlow()

    fun saveAuthToken(token: String, username: String, email: String, userId: Int) {
        prefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isLoggedIn(): Boolean = !getAuthToken().isNullOrBlank()

    fun clearAuth() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .remove(KEY_USER_ID)
            .apply()
    }

    fun saveBaseUrl(url: String) {
        prefs.edit().putString(KEY_API_BASE_URL, url).apply()
        _baseUrlFlow.value = url
    }

    fun getBaseUrl(): String {
        return prefs.getString(KEY_API_BASE_URL, BuildConfig.DEFAULT_API_BASE_URL)
            ?: BuildConfig.DEFAULT_API_BASE_URL
    }

    fun setOnboardingDone(done: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    fun isOnboardingDone(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
}
