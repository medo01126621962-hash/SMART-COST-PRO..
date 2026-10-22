package com.smartcostpro.data.remote.models

import com.squareup.moshi.Json

data class LoginRequest(
    @param:Json(name = "identifier") val identifier: String,
    @param:Json(name = "password") val password: String,
    @param:Json(name = "role") val role: String = "",
    @param:Json(name = "turnstileToken") val turnstileToken: String = ""
)

data class RegisterRequest(
    @param:Json(name = "name") val name: String,
    @param:Json(name = "username") val username: String,
    @param:Json(name = "email") val email: String,
    @param:Json(name = "password") val password: String,
    @param:Json(name = "deviceId") val deviceId: String = "",
    @param:Json(name = "turnstileToken") val turnstileToken: String = ""
)

data class SelectRoleRequest(
    @param:Json(name = "roleChoiceTicket") val roleChoiceTicket: String,
    @param:Json(name = "role") val role: String
)

data class ForgotPasswordRequest(
    @param:Json(name = "email") val email: String
)

data class ResetPasswordRequest(
    @param:Json(name = "email") val email: String,
    @param:Json(name = "code") val code: String,
    @param:Json(name = "newPassword") val newPassword: String
)

data class UserDto(
    @param:Json(name = "id") val id: String? = null,
    @param:Json(name = "email") val email: String? = null,
    @param:Json(name = "username") val username: String? = null,
    @param:Json(name = "name") val name: String? = null,
    @param:Json(name = "display_name") val displayName: String? = null,
    @param:Json(name = "phone") val phone: String? = null,
    @param:Json(name = "phone_verified") val phoneVerified: Boolean? = null,
    @param:Json(name = "role") val role: String? = null,
    @param:Json(name = "activation_status") val activationStatus: String? = null
)

data class AuthResponse(
    @param:Json(name = "token") val token: String? = null,
    @param:Json(name = "user") val user: UserDto? = null,
    @param:Json(name = "requiresRoleChoice") val requiresRoleChoice: Boolean? = null,
    @param:Json(name = "availableRoles") val availableRoles: List<String>? = null,
    @param:Json(name = "roleChoiceTicket") val roleChoiceTicket: String? = null,
    @param:Json(name = "error") val error: String? = null,
    @param:Json(name = "message") val message: String? = null,
    @param:Json(name = "alreadyExists") val alreadyExists: Boolean? = null,
    @param:Json(name = "ok") val ok: Boolean? = null,
    @param:Json(name = "emailSent") val emailSent: Boolean? = null
)
