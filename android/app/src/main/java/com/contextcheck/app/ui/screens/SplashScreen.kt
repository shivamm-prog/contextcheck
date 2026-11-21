package com.contextcheck.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.local.AppPreferences
import com.contextcheck.app.ui.navigation.NavRoutes
import com.contextcheck.app.ui.theme.DarkCharcoal
import com.contextcheck.app.ui.theme.SageGreen
import com.contextcheck.app.ui.theme.SoftMint
import com.contextcheck.app.ui.theme.WarmWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    preferences: AppPreferences,
    onNavigate: (String) -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(1200)

        // Determine starting screen based on login & onboarding state
        if (!preferences.isLoggedIn() && !preferences.isOnboardingDone()) {
            onNavigate(NavRoutes.AUTH)
        } else if (!preferences.isOnboardingDone()) {
            onNavigate(NavRoutes.PERMISSION_SETUP)
        } else {
            onNavigate(NavRoutes.DASHBOARD)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Elegant Sage Green Shield Icon Container
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "ContextCheck Shield",
                    tint = SageGreen,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name
            Text(
                text = "ContextCheck",
                style = MaterialTheme.typography.headlineLarge,
                color = DarkCharcoal,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mandatory Tagline
            Text(
                text = "Think Before You Believe.\nVerify Before You Share.",
                style = MaterialTheme.typography.titleMedium,
                color = SageGreen,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Distinctive Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SoftMint)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .alpha(alpha.value)
            ) {
                Text(
                    text = "Context-Aware Forward Verification",
                    color = DarkCharcoal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
