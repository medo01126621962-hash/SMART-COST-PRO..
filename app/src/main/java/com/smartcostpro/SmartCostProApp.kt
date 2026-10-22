package com.smartcostpro

import android.app.Application
import androidx.room.Room
import com.smartcostpro.data.local.SessionManager
import com.smartcostpro.data.local.SmartCostDatabase
import com.smartcostpro.data.remote.ApiClient
import com.smartcostpro.data.repository.AuthRepository
import com.smartcostpro.data.repository.AuthRepositoryImpl

class SmartCostProApp : Application() {

    lateinit var database: SmartCostDatabase
        private set

    lateinit var sessionManager: SessionManager
        private set

    lateinit var authRepository: AuthRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            SmartCostDatabase::class.java,
            SmartCostDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

        sessionManager = SessionManager(applicationContext)

        val apiService = ApiClient.createAuthService()
        authRepository = AuthRepositoryImpl(
            apiService = apiService,
            sessionManager = sessionManager
        )
    }

    companion object {
        lateinit var instance: SmartCostProApp
            private set
    }
}
