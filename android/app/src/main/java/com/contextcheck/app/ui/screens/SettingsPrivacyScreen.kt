package com.contextcheck.app.ui.screens

import android.content.Context
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.api.ApiClient
import com.contextcheck.app.data.local.AppPreferences
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.service.ContextCheckAccessibilityService
import com.contextcheck.app.ui.navigation.NavRoutes
import com.contextcheck.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPrivacyScreen(
    repository: ContextCheckRepository,
    preferences: AppPreferences,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var baseUrlInput by remember { mutableStateOf(preferences.getBaseUrl()) }
    var connectionStatus by remember { mutableStateOf<String?>(null) }
    var isCheckingConnection by remember { mutableStateOf(false) }

    val isAccessibilityActive = ContextCheckAccessibilityService.isServiceEnabled(context)
    val isOverlayActive = Settings.canDrawOverlays(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        },
        containerColor = WarmWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // BACKEND SERVER CONFIGURATION CARD (PHYSICAL DEVICE COMPATIBILITY)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = SageGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Backend Server Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Allows connecting to a local emulator, physical device via LAN IP, or remote FastAPI instance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = baseUrlInput,
                        onValueChange = { baseUrlInput = it },
                        label = { Text("API Base URL") },
                        placeholder = { Text("http://192.168.1.100:8000/") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { baseUrlInput = "http://10.0.2.2:8000/" },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Emulator", fontSize = 11.sp, color = DarkCharcoal)
                        }

                        OutlinedButton(
                            onClick = { baseUrlInput = "http://127.0.0.1:8000/" },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Localhost", fontSize = 11.sp, color = DarkCharcoal)
                        }

                        Button(
                            onClick = {
                                preferences.saveBaseUrl(baseUrlInput.trim())
                                ApiClient.updateBaseUrl(baseUrlInput.trim())
                                Toast.makeText(context, "API Base URL updated", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Save", fontSize = 12.sp, color = WarmWhite, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test Connection Button
                    OutlinedButton(
                        onClick = {
                            isCheckingConnection = true
                            connectionStatus = null
                            preferences.saveBaseUrl(baseUrlInput.trim())
                            ApiClient.updateBaseUrl(baseUrlInput.trim())

                            coroutineScope.launch {
                                val healthRes = repository.checkHealth()
                                isCheckingConnection = false
                                if (healthRes.isSuccess) {
                                    val h = healthRes.getOrNull()
                                    connectionStatus = "Connected: ${h?.service} v${h?.version} (Healthy)"
                                } else {
                                    connectionStatus = "Error: Unable to reach ${baseUrlInput.trim()}"
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isCheckingConnection) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Test Server Connection (/health)", color = DarkCharcoal, fontSize = 13.sp)
                    }

                    connectionStatus?.let { status ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = status,
                            color = if (status.startsWith("Connected")) SageGreen else VerdictFalse,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // PERMISSIONS OVERVIEW CARD
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Permission Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                        TextButton(onClick = { onNavigate(NavRoutes.PERMISSION_SETUP) }) {
                            Text("Reconfigure", color = SageGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    PermissionStatusRow("Accessibility Service", isAccessibilityActive)
                    Spacer(modifier = Modifier.height(6.dp))
                    PermissionStatusRow("Verification Pill Overlay", isOverlayActive)
                }
            }

            // PRIVACY DISCLOSURE SECTION (MANDATORY REQUIREMENT)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceTint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SageGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Privacy & Surveillance Protection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PrivacyBullet(
                        title = "No Continuous Surveillance",
                        desc = "ContextCheck never runs background listening, chat scraping, or conversation surveillance."
                    )
                    PrivacyBullet(
                        title = "User-Initiated Verification Only",
                        desc = "Content is processed only after you tap the ContextCheck verification action."
                    )
                    PrivacyBullet(
                        title = "No Secret Audio Recording",
                        desc = "Microphone is never used in the background; voice messages require explicit user forward."
                    )
                    PrivacyBullet(
                        title = "Transparent Storage & Control",
                        desc = "Only verification reports are stored. You can clear your complete history at any time."
                    )
                }
            }

            // ACCOUNT & LOGOUT
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Account: ${preferences.getUsername() ?: "Guest User"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkCharcoal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = preferences.getEmail() ?: "Local demo session",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            preferences.clearAuth()
                            onNavigate(NavRoutes.AUTH)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdictFalse.copy(alpha = 0.9f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = WarmWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out & Switch Account", color = WarmWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PermissionStatusRow(label: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = DarkCharcoal)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isEnabled) SoftMint else VerdictFalse.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isEnabled) "Active" else "Disabled",
                color = if (isEnabled) SageGreen else VerdictFalse,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun PrivacyBullet(title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SageGreen,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, color = DarkCharcoal, fontSize = 13.sp)
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MutedSlate, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}
