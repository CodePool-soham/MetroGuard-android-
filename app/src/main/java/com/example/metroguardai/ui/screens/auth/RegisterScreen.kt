package com.example.metroguardai.ui.screens.auth

import android.app.Activity
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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
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

        LaunchedEffect(uiState.isRegisterSuccess) {
            if (uiState.isRegisterSuccess) {
                onRegisterSuccess()
                viewModel.resetSuccessFlags()
            }
        }

        RegisterContent(
            uiState = uiState,
            onRegisterClick = { name, email, password ->
                viewModel.register(name, email, password)
            },
            onLoginClick = onLoginClick,
            onFieldChange = { viewModel.clearError() }
        )
    }
}

@Composable
fun RegisterContent(
    uiState: AuthUiState,
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onFieldChange: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val fullBrandingText = "MetroGuard AI"
    var displayedBrandingText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fullBrandingText.forEachIndexed { index, _ ->
            displayedBrandingText = fullBrandingText.substring(0, index + 1)
            delay(120.milliseconds)
        }
    }

    val isFormValid = name.isNotBlank() && 
                      email.isNotBlank() && 
                      password.isNotBlank() && 
                      confirmPassword == password

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
                            text = "Official Inspector Registration",
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
                        value = name,
                        onValueChange = { 
                            name = it
                            onFieldChange()
                        },
                        label = "FULL NAME",
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            onFieldChange()
                        },
                        label = "OFFICIAL EMAIL",
                        keyboardType = KeyboardType.Email,
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            onFieldChange()
                        },
                        label = "CREATE PASSWORD",
                        isPassword = true,
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            onFieldChange()
                        },
                        label = "CONFIRM PASSWORD",
                        isPassword = true,
                        enabled = !uiState.isLoading,
                        isError = confirmPassword.isNotEmpty() && confirmPassword != password
                    )

                    if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
                        )
                    }

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

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { onRegisterClick(name, email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !uiState.isLoading && isFormValid
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("REGISTER ACCOUNT", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = onLoginClick,
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = "Already have an account? Sign In",
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
