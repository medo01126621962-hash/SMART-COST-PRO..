package com.smartcostpro.ui.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Biometric helper using an Android Keystore key and a BiometricPrompt CryptoObject.
 * If cryptographic authentication cannot be prepared, biometric login is refused and
 * the caller should let the user use ordinary credential login instead.
 */
object BiometricHelper {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val BIOMETRIC_KEY_NAME = "smart_cost_pro_biometric_key"
    private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"

    fun isBiometricAvailable(context: Context, isArabic: Boolean = true): Pair<Boolean, String?> {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Pair(true, null)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Pair(
                false,
                if (isArabic) "لا يتوفر مستشعر بصمة مناسب على هذا الجهاز"
                else "No suitable biometric hardware found on this device"
            )
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Pair(
                false,
                if (isArabic) "مستشعر البصمة غير متاح حاليًا"
                else "Biometric hardware is currently unavailable"
            )
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Pair(
                false,
                if (isArabic) "لم يتم تسجيل بصمة في إعدادات الجهاز"
                else "No biometric credentials are enrolled on this device"
            )
            else -> Pair(
                false,
                if (isArabic) "التحقق بالبصمة غير مدعوم على هذا الجهاز"
                else "Biometric authentication is not supported on this device"
            )
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(BIOMETRIC_KEY_NAME)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenSpec = KeyGenParameterSpec.Builder(
                BIOMETRIC_KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()
            keyGenerator.init(keyGenSpec)
            return keyGenerator.generateKey()
        }
        return (keyStore.getEntry(BIOMETRIC_KEY_NAME, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun getInitializedCipher(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        return cipher
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: (BiometricPrompt.CryptoObject?) -> Unit,
        onError: (String) -> Unit
    ) {
        val cipher = try {
            getInitializedCipher()
        } catch (_: Exception) {
            onError("تعذر تهيئة الحماية الآمنة للبصمة. استخدم البريد الإلكتروني وكلمة المرور.")
            return
        }

        val promptInfo = try {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()
        } catch (_: Exception) {
            onError("تعذر تجهيز التحقق بالبصمة. استخدم تسجيل الدخول العادي.")
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val authenticatedCipher = result.cryptoObject
                    if (authenticatedCipher?.cipher == null) {
                        onError("لم يكتمل التحقق التشفيري للبصمة. استخدم تسجيل الدخول العادي.")
                    } else {
                        onSuccess(authenticatedCipher)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("لم يتم التعرف على البصمة، يرجى المحاولة مرة أخرى")
                }
            }
        )

        prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }
}
