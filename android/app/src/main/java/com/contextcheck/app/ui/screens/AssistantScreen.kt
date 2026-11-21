package com.contextcheck.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.contextcheck.app.data.repository.ContextCheckRepository
import com.contextcheck.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    repository: ContextCheckRepository,
    onBack: () -> Unit,
    onStartVerification: (String, String?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Text, 1: Image, 2: Video, 3: Audio

    var textInput by remember { mutableStateOf("") }
    var captionInput by remember { mutableStateOf("") }
    var claimedDate by remember { mutableStateOf("") }
    var claimedLocation by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ContextCheck Assistant", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
        ) {
            // Media Type Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceTint)
                    .padding(4.dp)
            ) {
                listOf("Text", "Image", "Video", "Audio").forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SageGreen else SurfaceTint)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) WarmWhite else DarkCharcoal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> {
                    // Text Verification Form
                    Text(
                        text = "Forwarded Message Content",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Paste forwarded WhatsApp text or rumor message...") },
                        minLines = 4,
                        maxLines = 8,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick multilingual sample chips
                    Text(
                        text = "Sample Multilingual Messages:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = { textInput = "हा video पुण्यातला आहे आणि आजचा आहे. Massive flooding near highway!" },
                            label = { Text("Marathi Mixed", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftMint)
                        )
                        SuggestionChip(
                            onClick = { textInput = "PM Free Recharge Scheme gives ₹500 free recharge to all Indian users." },
                            label = { Text("Govt Scheme", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftMint)
                        )
                    }
                }
                1 -> {
                    // Image Verification
                    Text(
                        text = "Image Verification & Caption Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = captionInput,
                        onValueChange = { captionInput = it },
                        placeholder = { Text("Enter accompanying forward caption (e.g. 'Photo of today's flood near Pune bridge')...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceTint),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                captionInput = "Photo from today's heavy rain and flooding in Pune near bus stand."
                                claimedLocation = "Pune"
                                claimedDate = "Today"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = SageGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Use Sample Pune Flood Image Test", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                2 -> {
                    // Video Verification
                    Text(
                        text = "Video Forward Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = captionInput,
                        onValueChange = { captionInput = it },
                        placeholder = { Text("Enter video claim (e.g. 'This video shows today's flooding in Pune')...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceTint),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                captionInput = "This video shows today's flooding in Pune."
                                claimedLocation = "Pune"
                                claimedDate = "Today"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = SageGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Use Pune Flood Video Forward (Demo 2)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                3 -> {
                    // Audio Verification
                    Text(
                        text = "Voice Note / Audio Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Enter voice note transcription or summary note...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SageGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceTint),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                textInput = "Forward this audio to everyone. School holiday declared tomorrow due to surprise inspection."
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = SageGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Use Sample WhatsApp Voice Note Rumor", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Context Metadata Fields (Core Innovation: Context-Aware Verification)
            Text(
                text = "Attached Context (Optional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = claimedDate,
                    onValueChange = { claimedDate = it },
                    label = { Text("Claimed Date") },
                    placeholder = { Text("e.g. Today") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = claimedLocation,
                    onValueChange = { claimedLocation = it },
                    label = { Text("Claimed Location") },
                    placeholder = { Text("e.g. Pune") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Start Verification Button
            Button(
                onClick = {
                    val contentToVerify = if (selectedTab == 0 || selectedTab == 3) {
                        textInput.ifBlank { "Forwarded message" }
                    } else {
                        captionInput.ifBlank { "Forwarded media caption" }
                    }
                    onStartVerification(contentToVerify, claimedLocation.ifBlank { null })
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WarmWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify with ContextCheck",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = WarmWhite
                )
            }
        }
    }
}
