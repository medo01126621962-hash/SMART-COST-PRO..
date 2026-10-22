package com.smartcostpro

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.smartcostpro.ui.auth.LoginScreen
import com.smartcostpro.ui.auth.LoginViewModel
import com.smartcostpro.ui.home.HomeScreen
import com.smartcostpro.ui.theme.PearlWhite
import com.smartcostpro.ui.theme.SmartCostProTheme

class MainActivity : FragmentActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        val app = application as SmartCostProApp
        LoginViewModel.factory(
            app.authRepository,
            app.sessionManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCostProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PearlWhite
                ) {
                    val session by loginViewModel.currentSession.collectAsState()

                    Crossfade(
                        targetState = session != null,
                        label = "ScreenTransition"
                    ) { isLoggedIn ->
                        if (isLoggedIn) {
                            HomeScreen(
                                session = session,
                                onSignOut = {
                                    (application as SmartCostProApp).sessionManager.clearSession()
                                }
                            )
                        } else {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    // Session is updated in SessionManager, triggering Crossfade
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
