package com.example.metroguardai.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.metroguardai.ui.theme.MetroGuardAITheme
import com.example.metroguardai.viewmodel.AuthUiState

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MetroGuardAITheme {
        RegisterContent(
            uiState = AuthUiState(),
            onRegisterClick = { _, _, _ -> },
            onLoginClick = {},
            onFieldChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenLoadingPreview() {
    MetroGuardAITheme {
        RegisterContent(
            uiState = AuthUiState(isLoading = true),
            onRegisterClick = { _, _, _ -> },
            onLoginClick = {},
            onFieldChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenErrorPreview() {
    MetroGuardAITheme {
        RegisterContent(
            uiState = AuthUiState(error = "Registration failed: Email already exists"),
            onRegisterClick = { _, _, _ -> },
            onLoginClick = {},
            onFieldChange = {}
        )
    }
}
