package com.example.metroguardai.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.metroguardai.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onReportsClick: () -> Unit,
    onAssistantClick: () -> Unit
) {
    val userSession by viewModel.userSession.collectAsStateWithLifecycle(initialValue = null)
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("MetroGuard AI", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Metrology Compliance Portal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val newMode = when (themeMode) {
                            "system" -> if (isSystemDark) "light" else "dark"
                            "light" -> "dark"
                            "dark" -> "system"
                            else -> "system"
                        }
                        viewModel.setThemeMode(newMode)
                    }) {
                        val currentDark = when (themeMode) {
                            "light" -> false
                            "dark" -> true
                            else -> isSystemDark
                        }
                        Icon(
                            imageVector = if (currentDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val scope = this
            val isTablet = scope.maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isTablet) 32.dp else 16.dp, vertical = 16.dp)
            ) {
                // User Header Section
                UserHeader(
                    name = userSession?.name ?: "Inspector",
                    role = userSession?.role ?: "FIELD_OFFICER",
                    isDemo = userSession?.token == "demo-token"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Summary Section
                SummarySection()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Operational Services",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isTablet) {
                    // Tablet Layout: Grid for all services
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DashboardCard(
                            title = "Scan Product",
                            description = "Initiate Metrology Audit",
                            icon = Icons.Default.QrCodeScanner,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            onClick = onScanClick,
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        DashboardCard(
                            title = "AI Assistant",
                            description = "Legal metrology guidance",
                            icon = Icons.Default.SmartToy,
                            onClick = onAssistantClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DashboardCard(
                            title = "Audit History",
                            description = "Review past inspections",
                            icon = Icons.Default.History,
                            modifier = Modifier.weight(1f),
                            onClick = onHistoryClick
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        DashboardCard(
                            title = "Analytics",
                            description = "Compliance reports",
                            icon = Icons.Default.Analytics,
                            modifier = Modifier.weight(1f),
                            onClick = onReportsClick
                        )
                    }
                } else {
                    // Phone Layout: Vertical stack
                    DashboardCard(
                        title = "Scan Product",
                        description = "Initiate Metrology Audit",
                        icon = Icons.Default.QrCodeScanner,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = onScanClick,
                        isPrimary = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        DashboardCard(
                            title = "History",
                            description = "Past Checks",
                            icon = Icons.Default.History,
                            modifier = Modifier.weight(1f),
                            onClick = onHistoryClick
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        DashboardCard(
                            title = "Reports",
                            description = "Analytics",
                            icon = Icons.Default.Description,
                            modifier = Modifier.weight(1f),
                            onClick = onReportsClick
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DashboardCard(
                        title = "Legal Metrology Assistant",
                        description = "AI-powered compliance guidance",
                        icon = Icons.Default.SmartToy,
                        onClick = onAssistantClick
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun UserHeader(name: String, role: String, isDemo: Boolean) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isDemo) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "DEMO",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun SummarySection() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem("Inspections", "124")
            VerticalDivider()
            SummaryItem("Compliant", "112", color = MaterialTheme.colorScheme.primary)
            VerticalDivider()
            SummaryItem("Violations", "12", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
fun SummaryItem(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    isPrimary: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 4.dp else 1.dp),
        shape = RoundedCornerShape(if (isPrimary) 20.dp else 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(if (isPrimary) 24.dp else 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isPrimary) 48.dp else 40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPrimary) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isPrimary) MaterialTheme.colorScheme.onPrimary 
                               else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = if (isPrimary) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
