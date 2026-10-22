package com.smartcostpro.ui.auth

/**
 * Authentication Constants and External Integration Configuration.
 *
 * To enable real Google Sign-In with Credential Manager:
 * 1. Open Google Cloud Console (https://console.cloud.google.com).
 * 2. Navigate to "APIs & Services" > "Credentials".
 * 3. Create an "OAuth 2.0 Client ID" with Application type "Web application".
 * 4. Add your Android package name and SHA-1 fingerprint under Android client, or paste the Web Client ID below.
 */
object AuthConstants {
    /**
     * Web Client ID from Google Cloud Console.
     * Leave empty to show helpful setup guidance when tapped.
     */
    const val GOOGLE_WEB_CLIENT_ID = ""
}
