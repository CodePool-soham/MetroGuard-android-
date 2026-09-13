package com.example.metroguardai.domain.model

data class UserSession(
    val token: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val name: String? = null,
    val role: String? = null
) {
    val isLoggedIn: Boolean get() = !token.isNullOrEmpty()
}
