package com.aegisfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aegisfit.app.presentation.navigation.AegisFitBottomNavBar
import com.aegisfit.app.presentation.navigation.AegisFitNavHost
import com.aegisfit.app.presentation.navigation.Screen
import com.aegisfit.app.presentation.MainViewModel
import com.aegisfit.app.presentation.theme.NhtFitnessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !mainViewModel.state.value.isReady }
        
        enableEdgeToEdge()
        setContent {
            val mainState by mainViewModel.state.collectAsStateWithLifecycle()

            NhtFitnessTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Hide bottom bar on full-screen flows
                val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            AegisFitBottomNavBar(navController)
                        }
                    }
                ) { paddingValues ->
                    AegisFitNavHost(
                        navController = navController,
                        paddingValues = paddingValues,
                        isAuthenticated = mainState.isAuthenticated
                    )
                }
            }
        }
    }
}
