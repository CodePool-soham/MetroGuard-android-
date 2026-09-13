package com.example.metroguardai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metroguardai.data.api.RetrofitClient
import com.example.metroguardai.data.local.SessionManager
import com.example.metroguardai.data.repository.AuthRepository
import com.example.metroguardai.ui.screens.assistant.AssistantScreen
import com.example.metroguardai.ui.screens.auth.LoginScreen
import com.example.metroguardai.ui.screens.auth.RegisterScreen
import com.example.metroguardai.ui.screens.dashboard.DashboardScreen
import com.example.metroguardai.ui.screens.history.HistoryScreen
import com.example.metroguardai.ui.screens.reports.ReportsScreen
import com.example.metroguardai.ui.screens.scan.ScanScreen
import com.example.metroguardai.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Shared AuthViewModel instance for all auth-related screens and dashboard
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val apiService = RetrofitClient.getApiService(context)
                val sessionManager = SessionManager(context)
                val repository = AuthRepository(apiService, sessionManager)
                return AuthViewModel(repository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = { 
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onLoginClick = { navController.navigate("login") },
                onRegisterSuccess = { 
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onScanClick = { navController.navigate("scan") },
                onHistoryClick = { navController.navigate("history") },
                onReportsClick = { navController.navigate("reports") },
                onAssistantClick = { navController.navigate("assistant") }
            )
        }

        composable("scan") {
            ScanScreen(onBack = { navController.popBackStack() })
        }

        composable("history") {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable("reports") {
            ReportsScreen(onBack = { navController.popBackStack() })
        }

        composable("assistant") {
            AssistantScreen(onBack = { navController.popBackStack() })
        }
    }
}
