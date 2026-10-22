package com.smartcostpro.ui.auth

import com.smartcostpro.data.repository.AuthLoginResult
import com.smartcostpro.data.repository.AuthRepository
import com.smartcostpro.data.repository.ForgotPasswordResult
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.domain.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = AuthViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is login mode with empty fields`() {
        val state = viewModel.uiState.value
        assertEquals(AuthMode.LOGIN, state.mode)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `mode switching updates mode and resets errors`() {
        viewModel.onEmailChange("test@example.com")
        viewModel.switchMode(AuthMode.REGISTER)
        assertEquals(AuthMode.REGISTER, viewModel.uiState.value.mode)
        assertNull(viewModel.uiState.value.errorMessage)

        viewModel.switchMode(AuthMode.FORGOT_PASSWORD)
        assertEquals(AuthMode.FORGOT_PASSWORD, viewModel.uiState.value.mode)
    }

    @Test
    fun `empty email or password shows validation error on login submit`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `guest login succeeds and sets guest session`() = runTest {
        viewModel.loginAsGuest()
        testDispatcher.scheduler.advanceUntilIdle()

        val session = viewModel.currentSession.value
        assertNotNull(session)
        assertTrue(session!!.isGuest)
        assertEquals("guest", session.user.role)
    }

    @Test
    fun `register validates minimum password length`() = runTest {
        viewModel.switchMode(AuthMode.REGISTER)
        viewModel.onNameChange("Mamdouh Saad")
        viewModel.onUsernameChange("mamdouh")
        viewModel.onEmailChange("mamdouh@example.com")
        viewModel.onPasswordChange("12345") // under 8 chars
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessage!!.contains("8 أحرف"))
    }

    @Test
    fun `role choice flow sets roleChoiceTicket and displays role choice view`() = runTest {
        fakeRepository.nextLoginResult = Result.success(
            AuthLoginResult.RequiresRoleChoice(
                ticket = "ticket-xyz-123",
                availableRoles = listOf("owner", "user")
            )
        )
        viewModel.onEmailChange("admin@smartcost.pro")
        viewModel.onPasswordChange("password123")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showRoleChoice)
        assertEquals("ticket-xyz-123", viewModel.uiState.value.roleChoiceTicket)
    }

    @Test
    fun `server 404 error is properly conveyed to user`() = runTest {
        fakeRepository.nextLoginResult = Result.failure(
            Exception("تعذر الاتصال بالخادم: مسار المصادقة يعيد 404 (الخدمة غير مفعّلة حاليًا على السيرفر).")
        )
        viewModel.onEmailChange("user@smartcost.pro")
        viewModel.onPasswordChange("password123")
        viewModel.submit()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessage!!.contains("404"))
    }
}

class FakeAuthRepository : AuthRepository {
    private val _sessionState = MutableStateFlow<AuthSession?>(null)
    override val sessionState: StateFlow<AuthSession?> = _sessionState

    var nextLoginResult: Result<AuthLoginResult> = Result.failure(Exception("Not set"))
    var nextRegisterResult: Result<AuthSession> = Result.failure(Exception("Not set"))

    override suspend fun login(identifier: String, password: String): Result<AuthLoginResult> {
        val result = nextLoginResult
        if (result.isSuccess) {
            val v = result.getOrNull()
            if (v is AuthLoginResult.Success) {
                _sessionState.value = v.session
            }
        }
        return result
    }

    override suspend fun selectRole(ticket: String, role: String): Result<AuthSession> {
        val session = AuthSession(
            token = "ticket-token",
            user = AuthUser(id = "user-1", email = "test@example.com", name = "Test", role = role),
            isGuest = false
        )
        _sessionState.value = session
        return Result.success(session)
    }

    override suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): Result<AuthSession> = nextRegisterResult

    override suspend fun forgotPassword(email: String): Result<ForgotPasswordResult> {
        return Result.success(ForgotPasswordResult(emailSent = true, message = "Sent"))
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Result<Unit> = Result.success(Unit)

    override fun loginAsGuest(): AuthSession {
        val guest = AuthSession(
            token = "guest-token",
            user = AuthUser(
                id = "guest-1",
                email = "guest@smartcostpro.local",
                name = "Guest",
                role = "guest"
            ),
            isGuest = true
        )
        _sessionState.value = guest
        return guest
    }

    override suspend fun logout(): Result<Unit> {
        _sessionState.value = null
        return Result.success(Unit)
    }
}
