package com.aitaskorganizer.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitaskorganizer.app.di.NetworkModule
import com.aitaskorganizer.app.ui.components.BrutalBox
import com.aitaskorganizer.app.ui.theme.*

/**
 * Settings screen with app configuration options.
 */
@Composable
fun SettingsScreen() {
    var backendUrl by rememberSaveable { mutableStateOf(NetworkModule.getBackendUrl()) }
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var darkModeEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(backendUrl) {
        NetworkModule.setBackendUrl(backendUrl)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppDimens.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = BauhausBlack,
                modifier = Modifier.padding(top = AppDimens.ScreenTopPadding, bottom = 8.dp)
            )

            // ── Backend URL ──────────────────────────────
            SettingsSectionHeader("BACKEND SERVER")
            BrutalBox(backgroundColor = Color.White, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Cloud,
                            contentDescription = null,
                            tint = BauhausBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("API URL", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = backendUrl,
                        onValueChange = { backendUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BauhausYellow,
                            unfocusedIndicatorColor = BauhausBlack
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "USE 10.0.2.2 FOR ANDROID EMULATOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BauhausBlack.copy(alpha = 0.4f)
                    )
                }
            }

            // ── Notifications ────────────────────────────
            SettingsSectionHeader("NOTIFICATIONS")
            SettingsToggleCard(
                icon = Icons.Outlined.Notifications,
                title = "TASK REMINDERS",
                subtitle = "GET NOTIFIED BEFORE DEADLINES",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            // ── Appearance ──────────────────────────────
            SettingsSectionHeader("APPEARANCE")
            SettingsToggleCard(
                icon = Icons.Outlined.DarkMode,
                title = "DARK MODE",
                subtitle = "COMING SOON",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it },
                enabled = false
            )

            // ── Data ─────────────────────────────────────
            SettingsSectionHeader("DATA")
            BrutalBox(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppDimens.CornerRadiusSmall
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = BauhausRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CLEAR ALL DATA",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = BauhausRed
                        )
                        Text(
                            text = "DELETE ALL TASKS AND REMINDERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BauhausBlack.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // ── About ────────────────────────────────────
            SettingsSectionHeader("ABOUT")
            BrutalBox(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppDimens.CornerRadiusSmall
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = BauhausBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI TASK ORGANIZER",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "VERSION 1.0.0",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BauhausBlack.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        color = BauhausBlack.copy(alpha = 0.5f),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    BrutalBox(
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = AppDimens.CornerRadiusSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = BauhausBlack,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BauhausBlack.copy(alpha = 0.4f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SuccessGreen,
                    checkedTrackColor = SuccessGreen.copy(alpha = 0.5f),
                    uncheckedThumbColor = BauhausBlack,
                    uncheckedTrackColor = BauhausWhite
                )
            )
        }
    }
}
