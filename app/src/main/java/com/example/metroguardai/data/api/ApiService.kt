package com.example.metroguardai.data.api

import com.example.metroguardai.data.dto.LoginRequest
import com.example.metroguardai.data.dto.LoginResponse
import com.example.metroguardai.data.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}
