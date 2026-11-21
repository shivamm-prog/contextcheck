package com.contextcheck.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = WarmWhite,
    primaryContainer = SoftMint,
    onPrimaryContainer = DarkCharcoal,
    secondary = DarkCharcoal,
    onSecondary = WarmWhite,
    background = WarmWhite,
    onBackground = DarkCharcoal,
    surface = WarmWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceTint,
    onSurfaceVariant = DarkCharcoal,
    outline = BorderSubtle
)

@Composable
fun ContextCheckTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = WarmWhite.toArgb()
            window.navigationBarColor = WarmWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
