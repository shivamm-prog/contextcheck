package com.contextcheck.app.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("health")
    suspend fun checkHealth(): HealthResponse

    @POST("auth/register")
    suspend fun register(@Body req: UserRegisterRequest): TokenResponse

    @POST("auth/login")
    suspend fun login(@Body req: UserLoginRequest): TokenResponse

    @GET("auth/me")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String
    ): UserResponse

    @POST("verify/text")
    suspend fun verifyText(
        @Header("Authorization") authHeader: String?,
        @Body req: VerifyTextRequest
    ): VerificationResponse

    @POST("verify/image")
    suspend fun verifyImage(
        @Header("Authorization") authHeader: String?,
        @Body req: VerifyImageRequest
    ): VerificationResponse

    @POST("verify/video")
    suspend fun verifyVideo(
        @Header("Authorization") authHeader: String?,
        @Body req: VerifyVideoRequest
    ): VerificationResponse

    @POST("verify/audio")
    suspend fun verifyAudio(
        @Header("Authorization") authHeader: String?,
        @Body req: VerifyAudioRequest
    ): VerificationResponse

    @GET("history")
    suspend fun getHistory(
        @Header("Authorization") authHeader: String?,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<HistoryItemResponse>

    @GET("history/{id}")
    suspend fun getHistoryDetail(
        @Path("id") id: Int,
        @Header("Authorization") authHeader: String?
    ): HistoryItemResponse

    @DELETE("history/{id}")
    suspend fun deleteHistoryItem(
        @Path("id") id: Int,
        @Header("Authorization") authHeader: String?
    ): HistoryDeleteResponse

    @DELETE("history")
    suspend fun clearAllHistory(
        @Header("Authorization") authHeader: String?
    ): HistoryDeleteResponse
}
