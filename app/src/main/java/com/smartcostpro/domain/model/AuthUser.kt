package com.smartcostpro.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val username: String? = null,
    val name: String,
    val phone: String? = null,
    val phoneVerified: Boolean = false,
    val role: String = "user",
    val activationStatus: String = "active"
)

data class AuthSession(
    val token: String,
    val user: AuthUser,
    val isGuest: Boolean = false
)
