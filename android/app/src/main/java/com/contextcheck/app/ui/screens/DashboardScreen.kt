package com.contextcheck.app.ui.screens

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.api.HistoryItemResponse
import com.contextcheck.app.data.local.AppPreferences
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.service.ContextCheckAccessibilityService
import com.contextcheck.app.ui.navigation.NavRoutes
import com.contextcheck.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: ContextCheckRepository,
    preferences: AppPreferences,
    onNavigate: (String) -> Unit,
    onStartVerification: (String, String?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var recentHistory by remember { mutableStateOf<List<HistoryItemResponse>>(emptyList()) }
    var isAccessibilityActive by remember { mutableStateOf(ContextCheckAccessibilityService.isServiceEnabled(context)) }
    var isOverlayActive by remember { mutableStateOf(Settings.canDrawOverlays(context)) }

    LaunchedEffect(Unit) {
        isAccessibilityActive = ContextCheckAccessibilityService.isServiceEnabled(context)
        isOverlayActive = Settings.canDrawOverlays(context)
        val res = repository.getHistory()
        if (res.isSuccess) {
            recentHistory = res.getOrDefault(emptyList()).take(4)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ContextCheck",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Think Before You Believe. Verify Before You Share.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SageGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(NavRoutes.HISTORY) }) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = DarkCharcoal)
                    }
                    IconButton(onClick = { onNavigate(NavRoutes.SETTINGS_PRIVACY) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DarkCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(NavRoutes.ASSISTANT) },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = WarmWhite) },
                text = { Text("Verify Forward", fontWeight = FontWeight.Bold, color = WarmWhite) },
                containerColor = SageGreen,
                shape = RoundedCornerShape(16.dp)
            )
        },
        containerColor = WarmWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // WhatsApp 1-Tap Overlay Status Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAccessibilityActive && isOverlayActive) SoftMint else SurfaceTint
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SageGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAccessibilityActive) Icons.Default.Check else Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = WarmWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAccessibilityActive && isOverlayActive) "WhatsApp 1-Tap Active" else "WhatsApp Integration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = if (isAccessibilityActive && isOverlayActive)
                                    "Long-press message in WhatsApp → Tap ContextCheck once."
                                else "Accessibility or overlay permission required for 1-tap overlay.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkCharcoal.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }

                        if (!isAccessibilityActive || !isOverlayActive) {
                            TextButton(onClick = { onNavigate(NavRoutes.PERMISSION_SETUP) }) {
                                Text("Setup", color = SageGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // WhatsApp Live Simulator Launcher Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(NavRoutes.WHATSAPP_SIM) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = SageGreen)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Simulate WhatsApp 1-Tap Flow",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Experience long-press → instant ContextCheck verification",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedSlate,
                                fontSize = 12.sp
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedSlate)
                    }
                }
            }

            // Hackathon Demo Scenarios Section
            item {
                Text(
                    text = "Hackathon Demo Scenarios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
                Text(
                    text = "Tap any scenario to launch instant 1-tap verification.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate
                )
            }

            // Demo Scenario 1: False Government Recharge Scheme
            item {
                DemoScenarioCard(
                    title = "Demo 1: False Government Scheme",
                    subtitle = "Circulating WhatsApp scheme promising free ₹500 recharge.",
                    expectedVerdict = "FALSE",
                    verdictColor = VerdictFalse,
                    onClick = {
                        onStartVerification(
                            "Urgent! PM Free Recharge Scheme gives ₹500 free recharge to all Indian users. Click link to claim now!",
                            "WhatsApp Forward"
                        )
                    }
                )
            }

            // Demo Scenario 2: Pune Flood - Real Media, False Context (USP!)
            item {
                DemoScenarioCard(
                    title = "Demo 2: Real Media, False Context (USP)",
                    subtitle = "\"हा video पुण्यातला आहे आणि आजचा आहे.\" Genuine 2019 flood footage falsely captioned as today.",
                    expectedVerdict = "MISLEADING CONTEXT",
                    verdictColor = VerdictMisleading,
                    isUsp = true,
                    onClick = {
                        onStartVerification(
                            "हा video पुण्यातला आहे आणि आजचा आहे. Massive flooding near highway in Pune!",
                            "Video Forward"
                        )
                    }
                )
            }

            // Demo Scenario 3: Unverified Local Rumor
            item {
                DemoScenarioCard(
                    title = "Demo 3: Unverified Local Rumor",
                    subtitle = "School holiday announced tomorrow due to surprise inspection.",
                    expectedVerdict = "UNVERIFIED",
                    verdictColor = VerdictUnverified,
                    onClick = {
                        onStartVerification(
                            "Attention all parents: school holiday declared tomorrow due to surprise inspection in sector 4.",
                            "WhatsApp Group Note"
                        )
                    }
                )
            }

            // Recent Verifications Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Verifications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    TextButton(onClick = { onNavigate(NavRoutes.HISTORY) }) {
                        Text("View All", color = SageGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Recent Verifications Items
            if (recentHistory.isEmpty()) {
                item {
                    Text(
                        text = "No verifications yet. Tap a demo scenario or verify a forward.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate
                    )
                }
            } else {
                items(recentHistory) { item ->
                    RecentHistoryCard(item = item, onClick = {
                        // Open report
                        coroutineScope.launch {
                            val res = repository.getHistory()
                            onNavigate(NavRoutes.VERIFICATION_REPORT)
                        }
                    })
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun DemoScenarioCard(
    title: String,
    subtitle: String,
    expectedVerdict: String,
    verdictColor: Color,
    isUsp: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(verdictColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = expectedVerdict,
                        color = verdictColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            if (isUsp) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "★ PRIMARY USP: REAL MEDIA, FALSE CONTEXT",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to Verify →",
                    color = SageGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun RecentHistoryCard(
    item: HistoryItemResponse,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val badgeColor = when (item.verdict) {
                "FALSE" -> VerdictFalse
                "MISLEADING CONTEXT" -> VerdictMisleading
                "SUPPORTED" -> VerdictSupported
                else -> VerdictUnverified
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.claim,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = DarkCharcoal
                )
                Text(
                    text = "${item.verdict} • ${item.contentType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedSlate)
        }
    }
}
