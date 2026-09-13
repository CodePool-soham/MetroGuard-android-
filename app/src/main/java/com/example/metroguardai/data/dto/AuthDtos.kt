package com.example.metroguardai.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val id: String,
    val email: String,
    val name: String,
    val role: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
