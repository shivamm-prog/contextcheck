package com.contextcheck.app.data.api

import com.google.gson.annotations.SerializedName

// Verification Requests
data class VerifyTextRequest(
    @SerializedName("content") val content: String,
    @SerializedName("context_caption") val contextCaption: String? = null,
    @SerializedName("claimed_date") val claimedDate: String? = null,
    @SerializedName("claimed_location") val claimedLocation: String? = null,
    @SerializedName("source_platform") val sourcePlatform: String = "WhatsApp"
)

data class VerifyImageRequest(
    @SerializedName("caption") val caption: String,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("image_base64") val imageBase64: String? = null,
    @SerializedName("claimed_date") val claimedDate: String? = null,
    @SerializedName("claimed_location") val claimedLocation: String? = null
)

data class VerifyVideoRequest(
    @SerializedName("caption") val caption: String,
    @SerializedName("video_url") val videoUrl: String? = null,
    @SerializedName("claimed_date") val claimedDate: String? = null,
    @SerializedName("claimed_location") val claimedLocation: String? = null
)

data class VerifyAudioRequest(
    @SerializedName("transcript_text") val transcriptText: String? = null,
    @SerializedName("audio_url") val audioUrl: String? = null,
    @SerializedName("context_note") val contextNote: String? = null
)

// Verification Response
data class VerificationResponse(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("verdict") val verdict: String,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("claim") val claim: String,
    @SerializedName("evidence") val evidence: List<String> = emptyList(),
    @SerializedName("sources") val sources: List<String> = emptyList(),
    @SerializedName("date_comparison") val dateComparison: String = "",
    @SerializedName("location_comparison") val locationComparison: String = "",
    @SerializedName("context_comparison") val contextComparison: String = "",
    @SerializedName("recommended_action") val recommendedAction: String = "DON'T FORWARD",
    @SerializedName("language") val language: String = "en",
    @SerializedName("entities") val entities: List<String> = emptyList(),
    @SerializedName("suspicious_signals") val suspiciousSignals: List<String> = emptyList(),
    @SerializedName("content_type") val contentType: String = "TEXT",
    @SerializedName("is_demo") val isDemo: Boolean = false,
    @SerializedName("timestamp") val timestamp: String? = null
)

// Authentication
data class UserRegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("full_name") val fullName: String? = null
)

data class UserLoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
)

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("created_at") val createdAt: String
)

// History & System
data class HistoryItemResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("content_type") val contentType: String,
    @SerializedName("claim") val claim: String,
    @SerializedName("verdict") val verdict: String,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("recommended_action") val recommendedAction: String,
    @SerializedName("date_comparison") val dateComparison: String? = null,
    @SerializedName("location_comparison") val locationComparison: String? = null,
    @SerializedName("context_comparison") val contextComparison: String? = null,
    @SerializedName("evidence") val evidence: List<String> = emptyList(),
    @SerializedName("sources") val sources: List<String> = emptyList(),
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("is_demo") val isDemo: Boolean
)

data class HistoryDeleteResponse(
    @SerializedName("status") val status: String,
    @SerializedName("deleted_id") val deletedId: Int? = null,
    @SerializedName("message") val message: String
)

data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("service") val service: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("version") val version: String
)
