package com.contextcheck.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.api.VerificationResponse
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationReportScreen(
    repository: ContextCheckRepository,
    onBackToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val verification by repository.currentVerification.collectAsState()

    val report: VerificationResponse = verification ?: VerificationResponse(
        verdict = "UNVERIFIED",
        confidence = 0.5f,
        claim = "No verification data available",
        recommendedAction = "WAIT FOR OFFICIAL CONFIRMATION"
    )

    val verdictColor = when (report.verdict) {
        "FALSE" -> VerdictFalse
        "MISLEADING CONTEXT" -> VerdictMisleading
        "SUPPORTED" -> VerdictSupported
        else -> VerdictUnverified
    }

    val isUspScenario = report.verdict == "MISLEADING CONTEXT"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToDashboard) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Dashboard")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = "ContextCheck Report:\nVerdict: ${report.verdict}\nClaim: ${report.claim}\nAction: ${report.recommendedAction}\nCheck facts before sharing!"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Verification Report"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = DarkCharcoal)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // VERDICT BANNER CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = verdictColor.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(verdictColor)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = report.verdict,
                            color = WarmWhite,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    if (isUspScenario) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SoftMint)
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "REAL MEDIA, FALSE CONTEXT",
                                color = DarkCharcoal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Confidence: ${(report.confidence * 100).toInt()}% • Language: ${report.language}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkCharcoal.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // RECOMMENDED ACTION
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(verdictColor.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (report.verdict == "SUPPORTED") Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = verdictColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recommended: ${report.recommendedAction}",
                            color = DarkCharcoal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // CLAIM CARD
            ReportSectionCard(title = "Claim Analyzed", icon = Icons.Default.FormatQuote) {
                Text(
                    text = report.claim,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkCharcoal,
                    lineHeight = 22.sp
                )
            }

            // CONTEXT COMPARISON CARD (Core USP)
            if (report.contextComparison.isNotBlank()) {
                ReportSectionCard(
                    title = "Context Comparison (USP)",
                    icon = Icons.Default.CompareArrows,
                    highlight = true
                ) {
                    Text(
                        text = report.contextComparison,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = DarkCharcoal,
                        lineHeight = 21.sp
                    )
                }
            }

            // DATE & LOCATION COMPARISON CARD
            if (report.dateComparison.isNotBlank() || report.locationComparison.isNotBlank()) {
                ReportSectionCard(title = "Temporal & Spatial Comparison", icon = Icons.Default.Schedule) {
                    if (report.dateComparison.isNotBlank()) {
                        Text(
                            text = "Date Verification:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSlate,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = report.dateComparison,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkCharcoal
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (report.locationComparison.isNotBlank()) {
                        Text(
                            text = "Location Verification:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSlate,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = report.locationComparison,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkCharcoal
                        )
                    }
                }
            }

            // EVIDENCE FINDINGS CARD
            if (report.evidence.isNotEmpty()) {
                ReportSectionCard(title = "Evidence Findings", icon = Icons.Default.FactCheck) {
                    report.evidence.forEachIndexed { idx, ev ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = "• ",
                                color = SageGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = ev,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkCharcoal,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // VERIFIED SOURCES CARD
            if (report.sources.isNotEmpty()) {
                ReportSectionCard(title = "Verified Sources & Advisories", icon = Icons.Default.LibraryBooks) {
                    report.sources.forEach { src ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = src,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkCharcoal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ContextCheck Summary", "ContextCheck Result: ${report.verdict}\nClaim: ${report.claim}\n${report.contextComparison}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = DarkCharcoal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Summary", color = DarkCharcoal, fontSize = 13.sp)
                }

                Button(
                    onClick = onBackToDashboard,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Home", color = WarmWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ReportSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) SoftMint.copy(alpha = 0.5f) else CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
