package com.smartcostpro.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val resetCode: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val resetSent: Boolean = false,
    val showRoleChoice: Boolean = false,
    val roleChoiceTicket: String = "",
    val availableRoles: List<String> = emptyList(),
    val emailExistsInServer: Boolean = false,
    val serverStatusMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentSession: StateFlow<AuthSession?> = authRepository.sessionState
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.sessionState.value)

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null, emailExistsInServer = false) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, errorMessage = null) }
    }

    fun onUsernameChange(newUsername: String) {
        val clean = newUsername.replace("\\s".toRegex(), "").take(30)
        _uiState.update { it.copy(username = clean, errorMessage = null) }
    }

    fun onResetCodeChange(code: String) {
        _uiState.update { it.copy(resetCode = code, errorMessage = null) }
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun switchMode(newMode: AuthMode) {
        _uiState.update {
            it.copy(
                mode = newMode,
                errorMessage = null,
                successMessage = null,
                resetCode = "",
                resetSent = false,
                showRoleChoice = false
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        _uiState.update { it.copy(errorMessage = null, successMessage = null, isLoading = true) }

        viewModelScope.launch {
            when (state.mode) {
                AuthMode.REGISTER -> handleRegister(state)
                AuthMode.FORGOT_PASSWORD -> handleForgotPassword(state)
                AuthMode.LOGIN -> handleLogin(state)
            }
        }
    }

    private suspend fun handleLogin(state: AuthUiState) {
        val identifier = state.email.trim()
        val password = state.password
        if (identifier.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "أدخل بريدك الإلكتروني أو اسم المستخدم.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "أدخل كلمة المرور.") }
            return
        }

        val result = authRepository.login(identifier, password)
        result.fold(
            onSuccess = { loginResult ->
                when (loginResult) {
                    is AuthLoginResult.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, successMessage = "تم تسجيل الدخول بنجاح.")
                        }
                    }
                    is AuthLoginResult.RequiresRoleChoice -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                showRoleChoice = true,
                                roleChoiceTicket = loginResult.ticket,
                                availableRoles = loginResult.availableRoles
                            )
                        }
                    }
                }
            },
            onFailure = { error ->
                val msg = error.message ?: "تعذر تسجيل الدخول."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        )
    }

    private suspend fun handleRegister(state: AuthUiState) {
        val name = state.name.trim()
        val username = state.username.trim()
        val email = state.email.trim()
        val password = state.password

        if (name.length < 2) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "اكتب اسمًا صحيحًا.") }
            return
        }
        if (username.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "اسم المستخدم مطلوب.") }
            return
        }
        if (!email.contains("@")) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "أدخل بريدًا إلكترونيًا صحيحًا.") }
            return
        }
        if (password.length < 8) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "كلمة المرور يجب أن تكون 8 أحرف على الأقل.") }
            return
        }

        val result = authRepository.register(name, username, email, password)
        result.fold(
            onSuccess = {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تم إنشاء الحساب وتسجيل الدخول بنجاح.")
                }
            },
            onFailure = { error ->
                val msg = error.message ?: "تعذر إنشاء الحساب."
                val alreadyExists = msg.contains("مسجل بالفعل", ignoreCase = true)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = msg,
                        emailExistsInServer = alreadyExists
                    )
                }
            }
        )
    }

    private suspend fun handleForgotPassword(state: AuthUiState) {
        val email = state.email.trim()
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "أدخل بريدك الإلكتروني.") }
            return
        }

        if (!state.resetSent) {
            val result = authRepository.forgotPassword(email)
            result.fold(
                onSuccess = { res ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            resetSent = res.emailSent,
                            successMessage = res.message
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "تعذر إرسال رمز الاستعادة.")
                    }
                }
            )
        } else {
            val code = state.resetCode.trim()
            val newPassword = state.password
            if (code.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "أدخل رمز الاستعادة.") }
                return
            }
            if (newPassword.length < 8) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "كلمة المرور الجديدة يجب أن تكون 8 أحرف على الأقل.") }
                return
            }

            val result = authRepository.resetPassword(email, code, newPassword)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mode = AuthMode.LOGIN,
                            password = "",
                            resetCode = "",
                            resetSent = false,
                            successMessage = "تم تغيير كلمة المرور بنجاح. يمكنك تسجيل الدخول الآن."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "تعذر تغيير كلمة المرور.")
                    }
                }
            )
        }
    }

    fun chooseRole(role: String) {
        val ticket = _uiState.value.roleChoiceTicket
        if (ticket.isBlank()) {
            _uiState.update { it.copy(errorMessage = "انتهت صلاحية اختيار نوع الدخول. سجّل الدخول مرة أخرى.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.selectRole(ticket, role)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, showRoleChoice = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "تعذر اختيار نوع الدخول.") }
                }
            )
        }
    }

    fun loginAsGuest() {
        val session = authRepository.loginAsGuest()
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = "تم الدخول كزائر بنجاح (وضع أوفلاين محلي)."
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { AuthUiState() }
        }
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository) as T
                }
            }
    }
}
