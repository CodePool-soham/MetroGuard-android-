package com.example.metroguardai.data.repository

import com.example.metroguardai.data.api.ApiService
import com.example.metroguardai.data.dto.LoginRequest
import com.example.metroguardai.data.dto.RegisterRequest
import com.example.metroguardai.data.local.SessionManager
import com.example.metroguardai.domain.model.UserSession

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    val userSession = sessionManager.userSession
    val themeMode = sessionManager.themeMode

    suspend fun saveThemeMode(mode: String) {
        sessionManager.saveThemeMode(mode)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val userSession = UserSession(
                    token = loginResponse.token,
                    userId = loginResponse.id,
                    email = loginResponse.email,
                    name = loginResponse.name,
                    role = loginResponse.role
                )
                sessionManager.saveSession(userSession)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun demoLogin(): Result<Unit> {
        val demoSession = UserSession(
            token = "demo-token",
            userId = "demo-id",
            email = "demo@metroguard.ai",
            name = "Demo Inspector",
            role = "INSPECTOR"
        )
        sessionManager.saveSession(demoSession)
        return Result.success(Unit)
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
