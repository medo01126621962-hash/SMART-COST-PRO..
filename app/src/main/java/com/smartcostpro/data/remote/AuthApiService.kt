package com.smartcostpro.data.remote

import com.smartcostpro.data.remote.models.AuthResponse
import com.smartcostpro.data.remote.models.ForgotPasswordRequest
import com.smartcostpro.data.remote.models.LoginRequest
import com.smartcostpro.data.remote.models.RegisterRequest
import com.smartcostpro.data.remote.models.ResetPasswordRequest
import com.smartcostpro.data.remote.models.SelectRoleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/api/auth/select-role")
    suspend fun selectRole(@Body request: SelectRoleRequest): Response<AuthResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<AuthResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<AuthResponse>

    @GET("/api/auth/me")
    suspend fun getProfile(@Header("Authorization") authorization: String): Response<AuthResponse>

    @POST("/api/auth/logout")
    suspend fun logout(@Header("Authorization") authorization: String): Response<AuthResponse>
}
