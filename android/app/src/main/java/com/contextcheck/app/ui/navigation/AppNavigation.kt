package com.contextcheck.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.contextcheck.app.data.local.AppPreferences
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: ContextCheckRepository,
    preferences: AppPreferences,
    initialRoute: String = NavRoutes.SPLASH,
    initialVerifyContent: String? = null
) {
    var contentToVerify by remember { mutableStateOf(initialVerifyContent ?: "") }
    var claimedContext by remember { mutableStateOf<String?>(null) }

    // If launched directly with an intent extra for 1-tap verification
    LaunchedEffect(initialVerifyContent) {
        if (!initialVerifyContent.isNullOrBlank()) {
            contentToVerify = initialVerifyContent
            navController.navigate(NavRoutes.VERIFICATION_PROGRESS) {
                popUpTo(NavRoutes.SPLASH) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (!initialVerifyContent.isNullOrBlank()) NavRoutes.VERIFICATION_PROGRESS else initialRoute
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                preferences = preferences,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.AUTH) {
            AuthScreen(
                repository = repository,
                preferences = preferences,
                onAuthSuccess = {
                    navController.navigate(NavRoutes.PERMISSION_SETUP) {
                        popUpTo(NavRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.PERMISSION_SETUP) {
            PermissionSetupScreen(
                preferences = preferences,
                onComplete = {
                    navController.navigate(NavRoutes.DASHBOARD) {
                        popUpTo(NavRoutes.PERMISSION_SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                repository = repository,
                preferences = preferences,
                onNavigate = { route -> navController.navigate(route) },
                onStartVerification = { content, context ->
                    contentToVerify = content
                    claimedContext = context
                    navController.navigate(NavRoutes.VERIFICATION_PROGRESS)
                }
            )
        }

        composable(NavRoutes.ASSISTANT) {
            AssistantScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onStartVerification = { content, context ->
                    contentToVerify = content
                    claimedContext = context
                    navController.navigate(NavRoutes.VERIFICATION_PROGRESS)
                }
            )
        }

        composable(NavRoutes.VERIFICATION_PROGRESS) {
            VerificationProgressScreen(
                repository = repository,
                contentToVerify = contentToVerify,
                claimedContext = claimedContext,
                onComplete = {
                    navController.navigate(NavRoutes.VERIFICATION_REPORT) {
                        popUpTo(NavRoutes.VERIFICATION_PROGRESS) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.VERIFICATION_REPORT) {
            VerificationReportScreen(
                repository = repository,
                onBackToDashboard = {
                    navController.navigate(NavRoutes.DASHBOARD) {
                        popUpTo(NavRoutes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HISTORY) {
            HistoryScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onOpenReport = {
                    navController.navigate(NavRoutes.VERIFICATION_REPORT)
                }
            )
        }

        composable(NavRoutes.SETTINGS_PRIVACY) {
            SettingsPrivacyScreen(
                repository = repository,
                preferences = preferences,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.WHATSAPP_SIM) {
            WhatsAppDemoSimScreen(
                onBack = { navController.popBackStack() },
                onStartVerification = { content, context ->
                    contentToVerify = content
                    claimedContext = context
                    // ONE TAP starts verification immediately!
                    navController.navigate(NavRoutes.VERIFICATION_PROGRESS)
                }
            )
        }
    }
}
