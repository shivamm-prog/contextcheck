package com.contextcheck.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextcheck.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WhatsAppDemoSimScreen(
    onBack: () -> Unit,
    onStartVerification: (String, String?) -> Unit
) {
    var selectedMessageIndex by remember { mutableStateOf<Int?>(null) }

    val messages = listOf(
        SimulatedMessage(
            sender = "Uncle Sharma",
            time = "10:14 AM",
            text = "हा video पुण्यातला आहे आणि आजचा आहे. Massive flooding near highway in Pune!",
            isVideo = true,
            forwardCount = "Forwarded many times"
        ),
        SimulatedMessage(
            sender = "College Group",
            time = "11:30 AM",
            text = "Urgent! PM Free Recharge Scheme gives ₹500 free recharge to all Indian users. Click link to claim now!",
            isVideo = false,
            forwardCount = "Forwarded"
        ),
        SimulatedMessage(
            sender = "Parents Association",
            time = "01:05 PM",
            text = "Attention all parents: school holiday declared tomorrow due to surprise inspection in sector 4.",
            isVideo = false,
            forwardCount = null
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366)), // WhatsApp Green indicator
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Family & Friends Group", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text("WhatsApp Simulator (Interactive)", fontSize = 11.sp, color = MutedSlate)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        },
        containerColor = Color(0xFFECE5DD) // Classic WhatsApp wallpaper tint
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Instructive banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = SageGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Long-press any message to reveal the ContextCheck overlay, then tap once to verify.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = DarkCharcoal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                messages.forEachIndexed { index, msg ->
                    val isSelected = selectedMessageIndex == index

                    Box(modifier = Modifier.fillMaxWidth()) {
                        // WhatsApp Chat Bubble
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SoftMint else CardBackground
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .combinedClickable(
                                    onClick = {
                                        // Tap also toggles for easy simulator demo
                                        selectedMessageIndex = if (isSelected) null else index
                                    },
                                    onLongClick = {
                                        selectedMessageIndex = index
                                    }
                                )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                msg.forwardCount?.let { forwardTag ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.FastForward,
                                            contentDescription = null,
                                            tint = MutedSlate,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = forwardTag,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            fontSize = 11.sp,
                                            color = MutedSlate
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = msg.sender,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SageGreen
                                )

                                if (msg.isVideo) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = WarmWhite,
                                                modifier = Modifier.size(44.dp)
                                            )
                                            Text(
                                                text = "Video (Katraj Flooding 2019)",
                                                color = WarmWhite.copy(alpha = 0.8f),
                                                fontSize = 11.sp,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 14.sp,
                                    color = DarkCharcoal,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = msg.time,
                                    fontSize = 10.sp,
                                    color = MutedSlate,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }

                        // THE CONTEXTCHECK ACTION PILL (ONE-TAP VERIFICATION USP)
                        if (isSelected) {
                            Card(
                                shape = RoundedCornerShape(30.dp),
                                colors = CardDefaults.cardColors(containerColor = SageGreen),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 0.dp, y = (-20).dp)
                                    .padding(4.dp)
                                    .combinedClickable(
                                        onClick = {
                                            // ONE TAP STARTS VERIFICATION IMMEDIATELY
                                            onStartVerification(msg.text, if (msg.isVideo) "Pune" else null)
                                        }
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = WarmWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Verify with ContextCheck",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = WarmWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SimulatedMessage(
    val sender: String,
    val time: String,
    val text: String,
    val isVideo: Boolean,
    val forwardCount: String?
)
