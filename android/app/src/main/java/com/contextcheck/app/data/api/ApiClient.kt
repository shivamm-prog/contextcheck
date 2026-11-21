package com.contextcheck.app.data.api

import com.contextcheck.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var currentBaseUrl: String = BuildConfig.DEFAULT_API_BASE_URL
    private var cachedApiService: ApiService? = null

    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Synchronized
    fun getService(baseUrl: String? = null): ApiService {
        val targetUrl = normalizeUrl(baseUrl ?: currentBaseUrl)
        if (cachedApiService == null || targetUrl != currentBaseUrl) {
            currentBaseUrl = targetUrl
            val retrofit = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            cachedApiService = retrofit.create(ApiService::class.java)
        }
        return cachedApiService!!
    }

    fun updateBaseUrl(newUrl: String) {
        val normalized = normalizeUrl(newUrl)
        if (normalized != currentBaseUrl) {
            currentBaseUrl = normalized
            cachedApiService = null
        }
    }

    fun getBaseUrl(): String = currentBaseUrl

    private fun normalizeUrl(url: String): String {
        var trimmed = url.trim()
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "http://$trimmed"
        }
        if (!trimmed.endsWith("/")) {
            trimmed = "$trimmed/"
        }
        return trimmed
    }
}
