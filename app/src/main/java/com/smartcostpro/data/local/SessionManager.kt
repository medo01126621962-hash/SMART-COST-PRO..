package com.smartcostpro.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.smartcostpro.domain.model.AuthSession
import com.smartcostpro.domain.model.AuthUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Manages user sessions and device preferences.
 * Authentication tokens and user session details are stored only in encrypted preferences.
 * Non-sensitive preferences may use ordinary SharedPreferences.
 */
class SessionManager(context: Context) {

    private val securePrefs: SharedPreferences? = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: Exception) {
        // Fail closed for session persistence. Never store authentication tokens in plaintext.
        Log.w("SessionManager", "Encrypted session storage is unavailable; session will not persist.")
        null
    }

    // Reuse the existing encrypted preferences for settings when available to preserve them.
    // If encryption is unavailable, ordinary preferences are used for non-sensitive settings only.
    private val prefs: SharedPreferences = securePrefs
        ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _sessionState = MutableStateFlow<AuthSession?>(null)
    val sessionState: StateFlow<AuthSession?> = _sessionState.asStateFlow()

    private val _languageState = MutableStateFlow(loadLanguage())
    val languageState: StateFlow<String> = _languageState.asStateFlow()

    init {
        if (isRememberDeviceEnabled()) {
            _sessionState.value = loadSession()
        }
    }

    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId.isNullOrBlank()) {
            deviceId = "android-${UUID.randomUUID()}"
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun isRememberDeviceEnabled(): Boolean = prefs.getBoolean(KEY_REMEMBER_DEVICE, true)

    fun setRememberDeviceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMEMBER_DEVICE, enabled).apply()
    }

    fun getLastIdentifier(): String = prefs.getString(KEY_LAST_IDENTIFIER, "") ?: ""

    fun setLastIdentifier(identifier: String) {
        prefs.edit().putString(KEY_LAST_IDENTIFIER, identifier).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun loadLanguage(): String = prefs.getString(KEY_LANGUAGE, "ar") ?: "ar"

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _languageState.value = lang
    }

    fun saveSession(session: AuthSession, rememberDevice: Boolean = true) {
        setRememberDeviceEnabled(rememberDevice)
        val storage = securePrefs
        if (storage != null) {
            storage.edit().apply {
                putString(KEY_TOKEN, session.token)
                putString(KEY_USER_ID, session.user.id)
                putString(KEY_USER_EMAIL, session.user.email)
                putString(KEY_USER_NAME, session.user.name)
                putString(KEY_USER_USERNAME, session.user.username ?: "")
                putString(KEY_USER_PHONE, session.user.phone ?: "")
                putBoolean(KEY_PHONE_VERIFIED, session.user.phoneVerified)
                putString(KEY_USER_ROLE, session.user.role)
                putString(KEY_ACTIVATION_STATUS, session.user.activationStatus)
                putBoolean(KEY_IS_GUEST, session.isGuest)
                apply()
            }
        } else {
            // Keep this session in memory for the current run, but never persist it unencrypted.
            Log.w("SessionManager", "Session is memory-only because encrypted storage is unavailable.")
        }
        _sessionState.value = session
    }

    fun loadSession(): AuthSession? {
        val storage = securePrefs ?: return null
        val token = storage.getString(KEY_TOKEN, null) ?: return null
        val id = storage.getString(KEY_USER_ID, "") ?: ""
        val email = storage.getString(KEY_USER_EMAIL, "") ?: ""
        val name = storage.getString(KEY_USER_NAME, "") ?: ""
        val username = storage.getString(KEY_USER_USERNAME, null)?.takeIf { it.isNotBlank() }
        val phone = storage.getString(KEY_USER_PHONE, null)?.takeIf { it.isNotBlank() }
        val phoneVerified = storage.getBoolean(KEY_PHONE_VERIFIED, false)
        val role = storage.getString(KEY_USER_ROLE, "user") ?: "user"
        val activationStatus = storage.getString(KEY_ACTIVATION_STATUS, "active") ?: "active"
        val isGuest = storage.getBoolean(KEY_IS_GUEST, false)

        val user = AuthUser(
            id = id,
            email = email,
            username = username,
            phone = phone,
            name = name,
            phoneVerified = phoneVerified,
            role = role,
            activationStatus = activationStatus
        )
        return AuthSession(token = token, user = user, isGuest = isGuest)
    }

    fun createGuestSession(): AuthSession {
        val guestUser = AuthUser(
            id = "guest-${System.currentTimeMillis()}",
            email = "guest@smartcostpro.local",
            username = "guest_user",
            name = "Guest User (زائر)",
            role = "guest",
            activationStatus = "active"
        )
        val session = AuthSession(token = "guest-local-token", user = guestUser, isGuest = true)
        saveSession(session, rememberDevice = false)
        return session
    }

    fun clearSession() {
        securePrefs?.edit()?.apply {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            remove(KEY_USER_USERNAME)
            remove(KEY_USER_PHONE)
            remove(KEY_PHONE_VERIFIED)
            remove(KEY_USER_ROLE)
            remove(KEY_ACTIVATION_STATUS)
            remove(KEY_IS_GUEST)
            apply()
        }
        _sessionState.value = null
    }

    companion object {
        private const val ENCRYPTED_PREFS_NAME = "smart_cost_encrypted_sec_prefs"
        private const val PREFS_NAME = "smart_cost_fallback_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_USER_EMAIL = "auth_user_email"
        private const val KEY_USER_NAME = "auth_user_name"
        private const val KEY_USER_USERNAME = "auth_user_username"
        private const val KEY_USER_PHONE = "auth_user_phone"
        private const val KEY_PHONE_VERIFIED = "auth_phone_verified"
        private const val KEY_USER_ROLE = "auth_user_role"
        private const val KEY_ACTIVATION_STATUS = "auth_activation_status"
        private const val KEY_IS_GUEST = "auth_is_guest"
        private const val KEY_DEVICE_ID = "smart_cost_device_id"
        private const val KEY_REMEMBER_DEVICE = "smart_cost_remember_device"
        private const val KEY_LAST_IDENTIFIER = "smart_cost_last_identifier"
        private const val KEY_BIOMETRIC_ENABLED = "smart_cost_biometric_enabled"
        private const val KEY_LANGUAGE = "smart_cost_language"
    }
}
