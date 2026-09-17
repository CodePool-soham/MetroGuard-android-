package com.example.metroguardai.data.api

import com.example.metroguardai.data.dto.ComplianceResponse
import com.example.metroguardai.data.dto.LoginRequest
import com.example.metroguardai.data.dto.LoginResponse
import com.example.metroguardai.data.dto.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @Multipart
    @POST("api/compliance/analyze-image")
    suspend fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("manual_pack_width_cm") manualWidth: RequestBody?,
        @Part("manual_pack_height_cm") manualHeight: RequestBody?,
        @Part("is_molded") isMolded: RequestBody?
    ): Response<ComplianceResponse>
}
