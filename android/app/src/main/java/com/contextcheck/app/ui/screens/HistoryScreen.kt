package com.contextcheck.app.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.data.api.HistoryItemResponse
import com.contextcheck.app.data.api.VerificationResponse
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.ui.navigation.NavRoutes
import com.contextcheck.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    repository: ContextCheckRepository,
    onBack: () -> Unit,
    onOpenReport: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var historyItems by remember { mutableStateOf<List<HistoryItemResponse>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    fun refreshHistory() {
        coroutineScope.launch {
            isLoading = true
            val res = repository.getHistory()
            if (res.isSuccess) {
                historyItems = res.getOrDefault(emptyList())
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshHistory()
    }

    val filteredList = historyItems.filter {
        searchQuery.isBlank() || it.claim.contains(searchQuery, ignoreCase = true) || it.verdict.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = VerdictFalse)
                        }
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
                .padding(horizontal = 20.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search verifications by claim or verdict...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SageGreen) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SageGreen,
                    unfocusedBorderColor = BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SageGreen)
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HistoryToggleOff,
                            contentDescription = null,
                            tint = MutedSlate,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching records found" else "No verification history yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedSlate
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            onClick = {
                                repository.setCurrentVerification(
                                    VerificationResponse(
                                        id = item.id,
                                        verdict = item.verdict,
                                        confidence = item.confidence,
                                        claim = item.claim,
                                        evidence = item.evidence,
                                        sources = item.sources,
                                        dateComparison = item.dateComparison ?: "",
                                        locationComparison = item.locationComparison ?: "",
                                        contextComparison = item.contextComparison ?: "",
                                        recommendedAction = item.recommendedAction,
                                        contentType = item.contentType,
                                        isDemo = item.isDemo,
                                        timestamp = item.timestamp
                                    )
                                )
                                onOpenReport()
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteHistoryItem(item.id)
                                    historyItems = historyItems.filter { it.id != item.id }
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("This will permanently remove your stored verification records from ContextCheck.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        coroutineScope.launch {
                            repository.clearAllHistory()
                            historyItems = emptyList()
                        }
                    }
                ) {
                    Text("Clear All", color = VerdictFalse, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = DarkCharcoal)
                }
            }
        )
    }
}

@Composable
fun HistoryCard(
    item: HistoryItemResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val verdictColor = when (item.verdict) {
        "FALSE" -> VerdictFalse
        "MISLEADING CONTEXT" -> VerdictMisleading
        "SUPPORTED" -> VerdictSupported
        else -> VerdictUnverified
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(verdictColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.verdict,
                        color = verdictColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.contentType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = MutedSlate,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.claim,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkCharcoal,
                maxLines = 2
            )

            if (!item.contextComparison.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.contextComparison,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action: ${item.recommendedAction}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkCharcoal,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "View Report →",
                    style = MaterialTheme.typography.labelSmall,
                    color = SageGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
