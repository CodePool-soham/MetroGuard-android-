package com.example.metroguardai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.metroguardai.ui.navigation.AppNavigation
import com.example.metroguardai.ui.theme.MetroGuardAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetroGuardAITheme {
                AppNavigation()
            }
        }
    }
}
