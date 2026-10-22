package com.smartcostpro.ui.auth

data class LoginUiState(
    val identifier: String = "",
    val identifierError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberDevice: Boolean = true,
    val language: String = "ar", // "ar" or "en"
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val generalSuccess: String? = null,

    // Forgot Password Flow
    val showForgotPasswordDialog: Boolean = false,
    val forgotPasswordEmail: String = "",
    val forgotPasswordError: String? = null,
    val isForgotPasswordLoading: Boolean = false,

    // Register Flow
    val showRegisterDialog: Boolean = false,
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerError: String? = null,
    val isRegisterLoading: Boolean = false,

    // General Dialog / SnackBar Info
    val infoMessage: String? = null
)
