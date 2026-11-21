package com.contextcheck.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.contextcheck.app.ContextCheckApp
import com.contextcheck.app.ui.navigation.AppNavigation
import com.contextcheck.app.ui.theme.ContextCheckTheme
import com.contextcheck.app.ui.theme.WarmWhite

class MainActivity : ComponentActivity() {

    private var initialVerifyContent by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIncomingIntent(intent)

        val app = application as ContextCheckApp

        setContent {
            ContextCheckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmWhite
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        repository = app.repository,
                        preferences = app.preferences,
                        initialVerifyContent = initialVerifyContent
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            ACTION_VERIFY_NOW -> {
                val text = intent.getStringExtra(EXTRA_VERIFY_TEXT)
                if (!text.isNullOrBlank()) {
                    initialVerifyContent = text
                }
            }
            Intent.ACTION_PROCESS_TEXT -> {
                val selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                if (!selectedText.isNullOrBlank()) {
                    initialVerifyContent = selectedText
                }
            }
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (!sharedText.isNullOrBlank()) {
                        initialVerifyContent = sharedText
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_VERIFY_NOW = "com.contextcheck.app.ACTION_VERIFY_NOW"
        const val EXTRA_VERIFY_TEXT = "extra_verify_text"
    }
}
