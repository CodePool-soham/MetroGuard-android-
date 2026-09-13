package com.example.metroguardai.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.metroguardai.ui.theme.MetroGuardAITheme
import com.example.metroguardai.viewmodel.AuthUiState

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MetroGuardAITheme {
        LoginContent(
            uiState = AuthUiState(),
            onLoginClick = { _, _ -> },
            onDemoLoginClick = {},
            onRegisterClick = {},
            onEmailOrPasswordChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingPreview() {
    MetroGuardAITheme {
        LoginContent(
            uiState = AuthUiState(isLoading = true),
            onLoginClick = { _, _ -> },
            onDemoLoginClick = {},
            onRegisterClick = {},
            onEmailOrPasswordChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    MetroGuardAITheme {
        LoginContent(
            uiState = AuthUiState(error = "Invalid credentials"),
            onLoginClick = { _, _ -> },
            onDemoLoginClick = {},
            onRegisterClick = {},
            onEmailOrPasswordChange = {}
        )
    }
}
