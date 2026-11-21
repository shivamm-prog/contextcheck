package com.contextcheck.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VerificationProgressScreen(
    repository: ContextCheckRepository,
    contentToVerify: String,
    claimedContext: String?,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var progressPercent by remember { mutableStateOf(0.15f) }
    val coroutineScope = rememberCoroutineScope()

    val steps = listOf(
        "Extracting core claim & linguistic markers",
        "Cross-referencing news & official fact databases",
        "Analyzing temporal & geographical metadata",
        "Comparing media origin against circulated story",
        "Synthesizing Context-Aware Verification Report"
    )

    LaunchedEffect(Unit) {
        // Step 1
        currentStep = 0
        progressPercent = 0.2f
        delay(600)

        // Step 2
        currentStep = 1
        progressPercent = 0.45f

        // Launch real backend request concurrently
        val verifyJob = coroutineScope.launch {
            val lower = contentToVerify.lowercase()
            if (lower.contains("video") || lower.contains("हा video")) {
                repository.verifyVideo(contentToVerify, claimedLocation = claimedContext)
            } else {
                repository.verifyText(contentToVerify, claimedLocation = claimedContext)
            }
        }

        delay(700)

        // Step 3
        currentStep = 2
        progressPercent = 0.70f
        delay(600)

        // Step 4
        currentStep = 3
        progressPercent = 0.90f
        verifyJob.join() // Wait for backend verification result

        // Step 5
        currentStep = 4
        progressPercent = 1.0f
        delay(500)

        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated Pulse Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progressPercent },
                    color = SageGreen,
                    trackColor = SurfaceTint,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(76.dp)
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Verifying Context...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Think Before You Believe. Verify Before You Share.",
                style = MaterialTheme.typography.bodyMedium,
                color = SageGreen,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Progress Steps Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    steps.forEachIndexed { index, stepLabel ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            index < currentStep -> SageGreen
                                            index == currentStep -> SoftMint
                                            else -> SurfaceTint
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index < currentStep) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = WarmWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (index == currentStep) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        color = SageGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = stepLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Normal,
                                color = if (index <= currentStep) DarkCharcoal else MutedSlate,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Subdued preview of content being analyzed
            Text(
                text = "\"${contentToVerify.take(80)}${if (contentToVerify.length > 80) "..." else ""}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                maxLines = 2
            )
        }
    }
}
