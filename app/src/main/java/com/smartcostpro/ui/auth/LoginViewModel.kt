package com.smartcostpro.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartcostpro.data.local.SessionManager
import com.smartcostpro.data.repository.AuthLoginResult
import com.smartcostpro.data.repository.AuthRepository
import com.smartcostpro.domain.model.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            identifier = sessionManager.getLastIdentifier(),
            rememberDevice = sessionManager.isRememberDeviceEnabled(),
            language = sessionManager.loadLanguage()
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val currentSession: StateFlow<AuthSession?> = authRepository.sessionState
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.sessionState.value)

    fun onIdentifierChange(newIdentifier: String) {
        _uiState.update {
            it.copy(
                identifier = newIdentifier,
                identifierError = null,
                generalError = null
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRememberDevice() {
        val next = !_uiState.value.rememberDevice
        _uiState.update { it.copy(rememberDevice = next) }
        sessionManager.setRememberDeviceEnabled(next)
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.language == "ar") "en" else "ar"
        _uiState.update { it.copy(language = nextLang) }
        sessionManager.setLanguage(nextLang)
    }

    fun dismissInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun showInfoMessage(msg: String) {
        _uiState.update { it.copy(infoMessage = msg) }
    }

    // Validation Helpers (Public for Unit Testing)
    fun validateIdentifierInput(id: String, isArabic: Boolean): String? {
        val trimmed = id.trim()
        if (trimmed.isEmpty()) {
            return if (isArabic) "يرجى إدخال اسم المستخدم أو البريد الإلكتروني" else "Please enter username or email"
        }
        if (trimmed.contains("@")) {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            if (!emailRegex.matches(trimmed)) {
                return if (isArabic) "صيغة البريد الإلكتروني غير صحيحة" else "Invalid email address format"
            }
        } else if (trimmed.length < 3) {
            return if (isArabic) "اسم المستخدم أو المعرف يجب ألا يقل عن 3 أحرف أو أرقام" else "Identifier must be at least 3 characters"
        }
        return null
    }

    fun validatePasswordInput(pwd: String, isArabic: Boolean): String? {
        if (pwd.isEmpty()) {
            return if (isArabic) "يرجى إدخال كلمة المرور" else "Please enter your password"
        }
        if (pwd.length < 6) {
            return if (isArabic) "كلمة المرور يجب ألا تقل عن 6 خانات" else "Password must be at least 6 characters"
        }
        return null
    }

    fun login(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        val state = _uiState.value
        val isArabic = state.language == "ar"

        val idError = validateIdentifierInput(state.identifier, isArabic)
        val pwdError = validatePasswordInput(state.password, isArabic)

        if (idError != null || pwdError != null) {
            _uiState.update {
                it.copy(
                    identifierError = idError,
                    passwordError = pwdError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, generalError = null, generalSuccess = null) }

        viewModelScope.launch {
            val result = authRepository.login(state.identifier.trim(), state.password)
            result.onSuccess { loginResult ->
                _uiState.update { it.copy(isLoading = false) }
                when (loginResult) {
                    is AuthLoginResult.Success -> {
                        sessionManager.setLastIdentifier(state.identifier.trim())
                        sessionManager.saveSession(loginResult.session, state.rememberDevice)
                        onSuccess()
                    }
                    is AuthLoginResult.RequiresRoleChoice -> {
                        // Automatically confirm owner/user role for direct access
                        val roleToPick = loginResult.availableRoles.firstOrNull() ?: "owner"
                        authRepository.selectRole(loginResult.ticket, roleToPick)
                        onSuccess()
                    }
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = ex.localizedMessage ?: if (isArabic) "فشل تسجيل الدخول" else "Login failed"
                    )
                }
            }
        }
    }

    fun loginWithBiometrics(onSuccess: () -> Unit) {
        val isArabic = _uiState.value.language == "ar"
        val savedSession = sessionManager.loadSession()
        if (savedSession != null) {
            // Restore saved session and enter
            sessionManager.saveSession(savedSession, rememberDevice = true)
            onSuccess()
        } else {
            _uiState.update {
                it.copy(
                    infoMessage = if (isArabic) {
                        "لتفعيل الدخول بالبصمة، يرجى تسجيل الدخول باسم المستخدم وكلمة المرور مع تفعيل 'تذكر هذا الجهاز' لأول مرة."
                    } else {
                        "To use biometric login, please sign in with credentials and enable 'Remember this device' first."
                    }
                )
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        authRepository.loginAsGuest()
        onSuccess()
    }

    fun onGoogleSignInClicked() {
        val isArabic = _uiState.value.language == "ar"
        if (AuthConstants.GOOGLE_WEB_CLIENT_ID.isBlank()) {
            _uiState.update {
                it.copy(
                    infoMessage = if (isArabic) {
                        "غير مفعّل بعد: يلزم إعداد Google Web Client ID من Google Cloud Console. يمكنك المتابعة بتسجيل الدخول المباشر أو كزائر."
                    } else {
                        "Not configured yet: Google Web Client ID is required from Google Cloud Console. You may continue with credentials or as guest."
                    }
                )
            }
        }
    }

    // Forgot Password Dialog Controls
    fun openForgotPasswordDialog() {
        _uiState.update {
            it.copy(
                showForgotPasswordDialog = true,
                forgotPasswordEmail = it.identifier.takeIf { id -> id.contains("@") } ?: "",
                forgotPasswordError = null
            )
        }
    }

    fun closeForgotPasswordDialog() {
        _uiState.update { it.copy(showForgotPasswordDialog = false, forgotPasswordError = null) }
    }

    fun onForgotPasswordEmailChange(email: String) {
        _uiState.update { it.copy(forgotPasswordEmail = email, forgotPasswordError = null) }
    }

    fun submitForgotPassword() {
        val state = _uiState.value
        val isArabic = state.language == "ar"
        val email = state.forgotPasswordEmail.trim()
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update {
                it.copy(
                    forgotPasswordError = if (isArabic) "يرجى إدخال بريد إلكتروني صحيح" else "Please enter a valid email"
                )
            }
            return
        }

        _uiState.update { it.copy(isForgotPasswordLoading = true, forgotPasswordError = null) }
        viewModelScope.launch {
            val res = authRepository.forgotPassword(email)
            res.onSuccess {
                _uiState.update { st ->
                    st.copy(
                        isForgotPasswordLoading = false,
                        showForgotPasswordDialog = false,
                        generalSuccess = if (isArabic) "تم إرسال رابط / كود الاستعادة إلى بريدك" else "Reset code sent to your email"
                    )
                }
            }.onFailure { ex ->
                _uiState.update { st ->
                    st.copy(
                        isForgotPasswordLoading = false,
                        forgotPasswordError = ex.localizedMessage ?: "حدث خطأ"
                    )
                }
            }
        }
    }

    // Register Dialog Controls
    fun openRegisterDialog() {
        _uiState.update {
            it.copy(
                showRegisterDialog = true,
                registerName = "",
                registerEmail = "",
                registerPassword = "",
                registerConfirmPassword = "",
                registerError = null
            )
        }
    }

    fun closeRegisterDialog() {
        _uiState.update { it.copy(showRegisterDialog = false, registerError = null) }
    }

    fun onRegisterNameChange(name: String) = _uiState.update { it.copy(registerName = name, registerError = null) }
    fun onRegisterEmailChange(email: String) = _uiState.update { it.copy(registerEmail = email, registerError = null) }
    fun onRegisterPasswordChange(pwd: String) = _uiState.update { it.copy(registerPassword = pwd, registerError = null) }
    fun onRegisterConfirmPasswordChange(pwd: String) = _uiState.update { it.copy(registerConfirmPassword = pwd, registerError = null) }

    fun submitRegister(onSuccess: () -> Unit) {
        val state = _uiState.value
        val isArabic = state.language == "ar"
        if (state.registerName.trim().length < 2) {
            _uiState.update { it.copy(registerError = if (isArabic) "يرجى كتابة الاسم بالكامل" else "Please enter full name") }
            return
        }
        if (state.registerEmail.trim().isEmpty() || !state.registerEmail.contains("@")) {
            _uiState.update { it.copy(registerError = if (isArabic) "يرجى كتابة بريد إلكتروني صحيح" else "Please enter a valid email") }
            return
        }
        if (state.registerPassword.length < 6) {
            _uiState.update { it.copy(registerError = if (isArabic) "كلمة المرور يجب ألا تقل عن 6 خانات" else "Password must be at least 6 characters") }
            return
        }
        if (state.registerPassword != state.registerConfirmPassword) {
            _uiState.update { it.copy(registerError = if (isArabic) "كلمتا المرور غير متطابقتين" else "Passwords do not match") }
            return
        }

        _uiState.update { it.copy(isRegisterLoading = true, registerError = null) }
        viewModelScope.launch {
            val username = state.registerEmail.substringBefore("@").replace(".", "_")
            val res = authRepository.register(
                name = state.registerName.trim(),
                username = username,
                email = state.registerEmail.trim(),
                password = state.registerPassword
            )
            res.onSuccess { session ->
                _uiState.update { it.copy(isRegisterLoading = false, showRegisterDialog = false) }
                sessionManager.saveSession(session, rememberDevice = true)
                onSuccess()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isRegisterLoading = false,
                        registerError = ex.localizedMessage ?: "فشل إنشاء الحساب"
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            sessionManager: SessionManager
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(authRepository, sessionManager) as T
            }
        }
    }
}
