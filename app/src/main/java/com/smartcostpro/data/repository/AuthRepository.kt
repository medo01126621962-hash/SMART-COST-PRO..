package com.smartcostpro.data.repository

import com.smartcostpro.data.local.SessionManager
import com.smartcostpro.data.remote.AuthApiService
import com.smartcostpro.data.remote.models.ForgotPasswordRequest
import com.smartcostpro.data.remote.models.LoginRequest
import com.smartcostpro.data.remote.models.RegisterRequest
import com.smartcostpro.data.remote.models.ResetPasswordRequest
import com.smartcostpro.data.remote.models.SelectRoleRequest
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.domain.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

sealed class AuthLoginResult {
    data class Success(val session: AuthSession) : AuthLoginResult()
    data class RequiresRoleChoice(val ticket: String, val availableRoles: List<String>) : AuthLoginResult()
}

data class ForgotPasswordResult(
    val emailSent: Boolean,
    val message: String
)

interface AuthRepository {
    val sessionState: StateFlow<AuthSession?>
    suspend fun login(identifier: String, password: String): Result<AuthLoginResult>
    suspend fun selectRole(ticket: String, role: String): Result<AuthSession>
    suspend fun register(name: String, username: String, email: String, password: String): Result<AuthSession>
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResult>
    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit>
    fun loginAsGuest(): AuthSession
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val sessionState: StateFlow<AuthSession?> = sessionManager.sessionState

    override suspend fun login(identifier: String, password: String): Result<AuthLoginResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(identifier = identifier, password = password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.requiresRoleChoice == true && !body.roleChoiceTicket.isNullOrBlank()) {
                        return@withContext Result.success(
                            AuthLoginResult.RequiresRoleChoice(
                                ticket = body.roleChoiceTicket,
                                availableRoles = body.availableRoles ?: listOf("owner", "user")
                            )
                        )
                    }

                    val token = body?.token
                    val userDto = body?.user
                    if (!token.isNullOrBlank() && userDto != null) {
                        val session = AuthSession(
                            token = token,
                            user = AuthUser(
                                id = userDto.id ?: "user-${System.currentTimeMillis()}",
                                email = userDto.email ?: identifier,
                                username = userDto.username,
                                name = userDto.name ?: userDto.displayName ?: identifier,
                                phone = userDto.phone,
                                phoneVerified = userDto.phoneVerified ?: false,
                                role = userDto.role ?: "user",
                                activationStatus = userDto.activationStatus ?: "active"
                            ),
                            isGuest = false
                        )
                        sessionManager.saveSession(session)
                        return@withContext Result.success(AuthLoginResult.Success(session))
                    } else {
                        val errorMsg = body?.error ?: body?.message ?: "بيانات الاستجابة غير مكتملة من الخادم."
                        return@withContext Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: IOException) {
                return@withContext Result.failure(
                    Exception("تعذر الاتصال بالخادم. تحقق من اتصال الإنترنت أو إعدادات الشبكة (${e.localizedMessage ?: "Network error"}).")
                )
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun selectRole(ticket: String, role: String): Result<AuthSession> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.selectRole(SelectRoleRequest(roleChoiceTicket = ticket, role = role))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token
                    val userDto = body?.user
                    if (!token.isNullOrBlank() && userDto != null) {
                        val session = AuthSession(
                            token = token,
                            user = AuthUser(
                                id = userDto.id ?: "user-${System.currentTimeMillis()}",
                                email = userDto.email ?: "",
                                username = userDto.username,
                                name = userDto.name ?: userDto.displayName ?: "User",
                                phone = userDto.phone,
                                phoneVerified = userDto.phoneVerified ?: false,
                                role = role,
                                activationStatus = userDto.activationStatus ?: "active"
                            ),
                            isGuest = false
                        )
                        sessionManager.saveSession(session)
                        return@withContext Result.success(session)
                    }
                    val msg = body?.error ?: body?.message ?: "فشل في تأكيد اختيار الدور."
                    return@withContext Result.failure(Exception(msg))
                } else {
                    val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    override suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): Result<AuthSession> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(
                RegisterRequest(
                    name = name.trim(),
                    username = username.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    deviceId = sessionManager.getDeviceId()
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                val userDto = body?.user
                if (!token.isNullOrBlank() && userDto != null) {
                    val session = AuthSession(
                        token = token,
                        user = AuthUser(
                            id = userDto.id ?: "user-${System.currentTimeMillis()}",
                            email = userDto.email ?: email,
                            username = username,
                            name = name,
                            phone = userDto.phone,
                            phoneVerified = userDto.phoneVerified ?: false,
                            role = userDto.role ?: "user",
                            activationStatus = userDto.activationStatus ?: "active"
                        ),
                        isGuest = false
                    )
                    sessionManager.saveSession(session)
                    return@withContext Result.success(session)
                }
                if (body?.alreadyExists == true) {
                    return@withContext Result.failure(Exception("هذا البريد مسجل بالفعل، يرجى تسجيل الدخول"))
                }
                val msg = body?.error ?: body?.message ?: "تعذر إتمام التسجيل على الخادم."
                return@withContext Result.failure(Exception(msg))
            } else {
                val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                return@withContext Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            return@withContext Result.failure(
                Exception("تعذر الاتصال بالخادم لإنشاء الحساب. تحقق من الشبكة.")
            )
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<ForgotPasswordResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.forgotPassword(ForgotPasswordRequest(email = email.trim().lowercase()))
                if (response.isSuccessful) {
                    val body = response.body()
                    val emailSent = body?.emailSent == true
                    val message = if (emailSent) {
                        "تم إرسال كود استعادة كلمة المرور إلى بريدك الإلكتروني"
                    } else {
                        body?.message ?: "لو البريد الإلكتروني مرتبط بحساب، سيصلك كود الاستعادة."
                    }
                    return@withContext Result.success(
                        ForgotPasswordResult(emailSent = emailSent, message = message)
                    )
                } else {
                    val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(
                    Exception("تعذر الاتصال بخدمة استعادة كلمة المرور (${e.localizedMessage ?: "Network error"}).")
                )
            }
        }

    override suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.resetPassword(
                ResetPasswordRequest(
                    email = email.trim().lowercase(),
                    code = code.trim(),
                    newPassword = newPassword
                )
            )
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                val errorMsg = parseErrorMessage(response.code(), response.errorBody()?.string())
                return@withContext Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    override fun loginAsGuest(): AuthSession {
        return sessionManager.createGuestSession()
    }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        val current = sessionManager.sessionState.value
        if (current != null && !current.isGuest && current.token.isNotBlank()) {
            try {
                apiService.logout("Bearer ${current.token}")
            } catch (_: Exception) {
                // Ignore network errors on logout
            }
        }
        sessionManager.clearSession()
        Result.success(Unit)
    }

    private fun parseErrorMessage(statusCode: Int, rawBody: String?): String {
        if (statusCode == 404) {
            return "تعذر الاتصال بالخادم: مسار المصادقة يعيد 404 (الخدمة غير مفعّلة حاليًا على السيرفر). يمكنك المتابعة كزائر لحين استقرار السيرفر."
        }
        if (statusCode == 401) {
            return "بيانات الدخول غير صحيحة. تأكد من البريد أو اسم المستخدم وكلمة المرور."
        }
        if (statusCode == 403) {
            return "غير مصرح لك بالدخول أو تم حظر الحساب."
        }
        if (statusCode == 429) {
            return "تم تجاوز عدد المحاولات المسموح به. يرجى الانتظار قليلاً."
        }
        if (!rawBody.isNullOrBlank()) {
            try {
                val json = JSONObject(rawBody)
                val err = json.optString("error", "").ifBlank { json.optString("message", "") }
                if (err.isNotBlank()) return err
            } catch (_: Exception) {
                // Fall through
            }
        }
        return "خطأ في الاتصال بالخادم (HTTP $statusCode)."
    }
}
