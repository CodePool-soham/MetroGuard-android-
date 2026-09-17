package com.example.metroguardai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.metroguardai.data.local.SessionManager
import com.example.metroguardai.ui.navigation.AppNavigation
import com.example.metroguardai.ui.theme.MetroGuardAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = SessionManager(this)
        setContent {
            val themeMode by sessionManager.themeMode.collectAsStateWithLifecycle(initialValue = "system")
            MetroGuardAITheme(themeMode = themeMode) {
                AppNavigation()
            }
        }
    }
}
