package com.smartcostpro.ui.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.smartcostpro.data.local.SessionManager
import com.smartcostpro.data.repository.AuthLoginResult
import com.smartcostpro.data.repository.AuthRepository
import com.smartcostpro.data.repository.ForgotPasswordResult
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.domain.model.AuthUser
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LoginViewModelTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sessionManager = SessionManager(context)
        sessionManager.clearSession()
        fakeAuthRepository = FakeAuthRepository(sessionManager)
        viewModel = LoginViewModel(fakeAuthRepository, sessionManager)
    }

    @Test
    fun `validateIdentifierInput with blank returns error in Arabic and English`() {
        val arError = viewModel.validateIdentifierInput("", isArabic = true)
        val enError = viewModel.validateIdentifierInput("   ", isArabic = false)

        assertNotNull(arError)
        assertEquals("يرجى إدخال اسم المستخدم أو البريد الإلكتروني", arError)

        assertNotNull(enError)
        assertEquals("Please enter username or email", enError)
    }

    @Test
    fun `validateIdentifierInput with invalid email format returns format error`() {
        val error = viewModel.validateIdentifierInput("user@invalid", isArabic = true)
        assertNotNull(error)
        assertEquals("صيغة البريد الإلكتروني غير صحيحة", error)
    }

    @Test
    fun `validateIdentifierInput with valid email returns null`() {
        val result = viewModel.validateIdentifierInput("CEO@smartcost.pro", isArabic = true)
        assertNull(result)
    }

    @Test
    fun `validateIdentifierInput with numeric employee code returns null`() {
        val result = viewModel.validateIdentifierInput("29401", isArabic = true)
        assertNull(result)
    }

    @Test
    fun `validateIdentifierInput with short username returns length error`() {
        val error = viewModel.validateIdentifierInput("ab", isArabic = true)
        assertNotNull(error)
        assertEquals("اسم المستخدم أو المعرف يجب ألا يقل عن 3 أحرف أو أرقام", error)
    }

    @Test
    fun `validatePasswordInput with blank returns error`() {
        val error = viewModel.validatePasswordInput("", isArabic = true)
        assertNotNull(error)
        assertEquals("يرجى إدخال كلمة المرور", error)
    }

    @Test
    fun `validatePasswordInput with short password returns length error`() {
        val error = viewModel.validatePasswordInput("12345", isArabic = true)
        assertNotNull(error)
        assertEquals("كلمة المرور يجب ألا تقل عن 6 خانات", error)
    }

    @Test
    fun `validatePasswordInput with valid password returns null`() {
        val result = viewModel.validatePasswordInput("Secret123!", isArabic = true)
        assertNull(result)
    }

    @Test
    fun `togglePasswordVisibility changes visibility state`() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `toggleLanguage switches between Arabic and English`() {
        assertEquals("ar", viewModel.uiState.value.language)
        viewModel.toggleLanguage()
        assertEquals("en", viewModel.uiState.value.language)
        viewModel.toggleLanguage()
        assertEquals("ar", viewModel.uiState.value.language)
    }

    @Test
    fun `toggleRememberDevice toggles state and persists in sessionManager`() {
        assertTrue(viewModel.uiState.value.rememberDevice)
        viewModel.toggleRememberDevice()
        assertFalse(viewModel.uiState.value.rememberDevice)
        assertFalse(sessionManager.isRememberDeviceEnabled())
    }

    @Test
    fun `login with network failure updates generalError message`() {
        fakeAuthRepository.shouldFail = true
        fakeAuthRepository.failureMessage = "خطأ في الاتصال بالخادم (HTTP 500)"

        viewModel.onIdentifierChange("CEO@smartcost.pro")
        viewModel.onPasswordChange("validPassword123")

        var successCalled = false
        viewModel.login { successCalled = true }

        assertFalse(successCalled)
        assertEquals("خطأ في الاتصال بالخادم (HTTP 500)", viewModel.uiState.value.generalError)
    }

    @Test
    fun `login success saves session and calls onSuccess`() {
        fakeAuthRepository.shouldFail = false

        viewModel.onIdentifierChange("CEO@smartcost.pro")
        viewModel.onPasswordChange("validPassword123")

        var successCalled = false
        viewModel.login { successCalled = true }

        assertTrue(successCalled)
        assertNull(viewModel.uiState.value.generalError)
        assertNotNull(sessionManager.loadSession())
        assertEquals("CEO@smartcost.pro", sessionManager.loadSession()?.user?.email)
    }

    @Test
    fun `biometric login without saved session sets informative message`() {
        sessionManager.clearSession()

        var successCalled = false
        viewModel.loginWithBiometrics { successCalled = true }

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.infoMessage)
        assertTrue(viewModel.uiState.value.infoMessage!!.contains("لتفعيل الدخول بالبصمة"))
    }

    @Test
    fun `biometric login with saved session succeeds immediately`() {
        val testUser = AuthUser("id-saved", "saved@test.com", null, "Saved User", null, true, "owner", "active")
        val savedSession = AuthSession("saved-token", testUser, false)
        sessionManager.saveSession(savedSession, rememberDevice = true)

        var successCalled = false
        viewModel.loginWithBiometrics { successCalled = true }

        assertTrue(successCalled)
        assertEquals("saved-token", sessionManager.sessionState.value?.token)
    }

    @Test
    fun `loginAsGuest creates guest session and invokes onSuccess`() {
        var successCalled = false
        viewModel.loginAsGuest { successCalled = true }

        assertTrue(successCalled)
        assertTrue(sessionManager.sessionState.value?.isGuest == true)
    }

    @Test
    fun `register validation catches short password and mismatch`() {
        viewModel.openRegisterDialog()
        viewModel.onRegisterNameChange("Mamdouh")
        viewModel.onRegisterEmailChange("mamdouh@test.com")
        viewModel.onRegisterPasswordChange("123")
        viewModel.onRegisterConfirmPasswordChange("123")

        viewModel.submitRegister { }
        assertEquals("كلمة المرور يجب ألا تقل عن 6 خانات", viewModel.uiState.value.registerError)

        viewModel.onRegisterPasswordChange("123456")
        viewModel.onRegisterConfirmPasswordChange("654321")
        viewModel.submitRegister { }
        assertEquals("كلمتا المرور غير متطابقتين", viewModel.uiState.value.registerError)
    }

    private class FakeAuthRepository(private val sessionManager: SessionManager) : AuthRepository {
        var shouldFail: Boolean = false
        var failureMessage: String = "Network error"

        override val sessionState: StateFlow<AuthSession?> = sessionManager.sessionState

        override suspend fun login(identifier: String, password: String): Result<AuthLoginResult> {
            if (shouldFail) {
                return Result.failure(Exception(failureMessage))
            }
            val user = AuthUser("id-1", identifier, null, "User", null, false, "user", "active")
            val session = AuthSession("tok-1", user, false)
            return Result.success(AuthLoginResult.Success(session))
        }

        override suspend fun selectRole(ticket: String, role: String): Result<AuthSession> {
            val user = AuthUser("id-1", "user@test.com", null, "User", null, false, role, "active")
            return Result.success(AuthSession("tok-1", user, false))
        }

        override suspend fun register(name: String, username: String, email: String, password: String): Result<AuthSession> {
            val user = AuthUser("id-1", email, username, name, null, false, "user", "active")
            return Result.success(AuthSession("tok-1", user, false))
        }

        override suspend fun forgotPassword(email: String): Result<ForgotPasswordResult> {
            return Result.success(ForgotPasswordResult(true, "Sent"))
        }

        override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> = Result.success(Unit)

        override fun loginAsGuest(): AuthSession = sessionManager.createGuestSession()

        override suspend fun logout(): Result<Unit> {
            sessionManager.clearSession()
            return Result.success(Unit)
        }
    }
}
