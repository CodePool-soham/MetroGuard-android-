package com.example.metroguardai.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.metroguardai.R
import com.example.metroguardai.ui.components.AuthTextField
import com.example.metroguardai.viewmodel.AuthUiState
import com.example.metroguardai.viewmodel.AuthViewModel
import com.example.metroguardai.ui.theme.MetroGuardAITheme
import com.example.metroguardai.ui.theme.JomhuriaFontFamily
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current

    // Force Light theme for Login/Register as requested by user
    MetroGuardAITheme(themeMode = "light") {
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = true
            }
        }

        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onLoginSuccess()
                viewModel.resetSuccessFlags()
            }
        }

        LoginContent(
            uiState = uiState,
            onLoginClick = { email, password -> viewModel.login(email, password) },
            onDemoLoginClick = { viewModel.demoLogin() },
            onRegisterClick = onRegisterClick,
            onEmailOrPasswordChange = { viewModel.clearError() }
        )
    }
}

@Composable
fun LoginContent(
    uiState: AuthUiState,
    onLoginClick: (String, String) -> Unit,
    onDemoLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onEmailOrPasswordChange: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val fullBrandingText = "MetroGuard AI"
    var displayedBrandingText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fullBrandingText.forEachIndexed { index, _ ->
            displayedBrandingText = fullBrandingText.substring(0, index + 1)
            delay(120.milliseconds)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val scope = this
            val isWide = scope.maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section with Branding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isWide) 280.dp else 320.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.main_bg_login_registration),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    Column(
                        modifier = Modifier.padding(top = if (isWide) 100.dp else 140.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayedBrandingText,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Legal Metrology Inspection System",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Form Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth(if (isWide) 0.6f else 1f)
                        .padding(horizontal = if (isWide) 0.dp else 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            onEmailOrPasswordChange()
                        },
                        label = "EMAIL ADDRESS",
                        keyboardType = KeyboardType.Email,
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onEmailOrPasswordChange()
                        },
                        label = "PASSWORD",
                        isPassword = true,
                        enabled = !uiState.isLoading
                    )

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("SIGN IN", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onDemoLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        Text("TRY DEMO LOGIN", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = onRegisterClick,
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = "New Inspector? Register Account",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
