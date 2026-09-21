package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.UserPreferences

@Composable
fun SettingsDialog(
  userPreferences: UserPreferences,
  onDismiss: () -> Unit,
  onUpdateUserName: (String) -> Unit,
  onUpdateThemeMode: (String) -> Unit,
  onUpdateFocusMinutes: (Int) -> Unit,
  onUpdateBreakMinutes: (Int) -> Unit,
  onClearAllData: () -> Unit,
  onReloadSampleData: () -> Unit
) {
  var userNameInput by remember { mutableStateOf(userPreferences.userName) }
  var showConfirmClear by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("settings_dialog"),
    title = {
      Text(
        text = "Preferences & Info",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // User Profile Name
        Column {
          Text(
            text = "User Name",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = userNameInput,
              onValueChange = { userNameInput = it },
              singleLine = true,
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
              },
              modifier = Modifier
                .weight(1f)
                .testTag("input_user_name"),
              shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { onUpdateUserName(userNameInput) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("save_user_name_button")
            ) {
              Text("Save")
            }
          }
        }

        // Theme Mode Selector
        Column {
          Text(
            text = "Theme Appearance",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            val modes = listOf(
              Triple("system", "System", Icons.Default.BrightnessAuto),
              Triple("light", "Light", Icons.Default.LightMode),
              Triple("dark", "Dark", Icons.Default.DarkMode)
            )

            modes.forEach { (modeKey, label, icon) ->
              val isSelected = userPreferences.themeMode == modeKey
              FilterChip(
                selected = isSelected,
                onClick = { onUpdateThemeMode(modeKey) },
                label = { Text(label) },
                leadingIcon = {
                  Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("theme_chip_$modeKey")
              )
            }
          }
        }

        // Focus Timer Interval
        Column {
          Text(
            text = "Focus Interval",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(20, 25, 30, 45).forEach { minutes ->
              FilterChip(
                selected = userPreferences.focusMinutes == minutes,
                onClick = { onUpdateFocusMinutes(minutes) },
                label = { Text("${minutes}m") },
                shape = RoundedCornerShape(10.dp)
              )
            }
          }
        }

        // Break Interval
        Column {
          Text(
            text = "Short Break Interval",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 15).forEach { minutes ->
              FilterChip(
                selected = userPreferences.breakMinutes == minutes,
                onClick = { onUpdateBreakMinutes(minutes) },
                label = { Text("${minutes}m") },
                shape = RoundedCornerShape(10.dp)
              )
            }
          }
        }

        // Reset and sample data actions
        Column {
          Text(
            text = "Data Management",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
              onClick = onReloadSampleData,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("load_sample_data_button")
            ) {
              Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reload Starter", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = { showConfirmClear = true },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("clear_data_button")
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear All", fontSize = 12.sp)
            }
          }
        }

        // Developer Attribution & Copyright Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Nova Productivity Workspace",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Version 1.0.0 • Pure Native Android",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Developer: Arpan Goswami",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "© 2026 Copyright Arpan Goswami. All rights reserved.",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
              textAlign = TextAlign.Center
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("close_settings_button")
      ) {
        Text("Done")
      }
    }
  )

  // Confirm clear data alert
  if (showConfirmClear) {
    AlertDialog(
      onDismissRequest = { showConfirmClear = false },
      title = { Text("Clear all data?") },
      text = { Text("This will permanently remove all tasks, notes, and focus history from your device.") },
      confirmButton = {
        Button(
          onClick = {
            showConfirmClear = false
            onClearAllData()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete Everything")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showConfirmClear = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
