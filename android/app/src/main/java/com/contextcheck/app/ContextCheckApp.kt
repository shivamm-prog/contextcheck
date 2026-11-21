package com.contextcheck.app

import android.app.Application
import com.contextcheck.app.data.api.ApiClient
import com.contextcheck.app.data.local.AppPreferences
import com.contextcheck.app.data.repository.ContextCheckRepository

class ContextCheckApp : Application() {

    lateinit var preferences: AppPreferences
        private set

    lateinit var repository: ContextCheckRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        preferences = AppPreferences(this)
        // Initialize API client with persisted base URL
        val savedBaseUrl = preferences.getBaseUrl()
        ApiClient.updateBaseUrl(savedBaseUrl)

        repository = ContextCheckRepository(preferences)
    }

    companion object {
        lateinit var instance: ContextCheckApp
            private set
    }
}
