package com.example.metroguardai.util

import com.example.metroguardai.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // We use runBlocking because OkHttp Interceptor is synchronous
        // and sessionManager uses Flow (coroutines)
        val token = runBlocking {
            sessionManager.userSession.first().token
        }
        
        return if (!token.isNullOrEmpty()) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
