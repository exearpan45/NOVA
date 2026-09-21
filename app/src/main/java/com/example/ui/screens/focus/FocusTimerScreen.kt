package com.example.ui.screens.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun FocusTimerScreen(
  timerMode: String,
  timeRemainingSeconds: Int,
  totalTimerSeconds: Int,
  isTimerRunning: Boolean,
  completedSessionsCount: Int,
  onModeChanged: (String) -> Unit,
  onStartTimer: () -> Unit,
  onPauseTimer: () -> Unit,
  onResetTimer: () -> Unit,
  modifier: Modifier = Modifier
) {
  val minutes = timeRemainingSeconds / 60
  val seconds = timeRemainingSeconds % 60
  val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

  val targetProgress = if (totalTimerSeconds > 0) {
    (totalTimerSeconds - timeRemainingSeconds).toFloat() / totalTimerSeconds.toFloat()
  } else 0f
  val animatedProgress by animateFloatAsState(targetValue = targetProgress, label = "timerProgress")

  val primaryColor = MaterialTheme.colorScheme.primary
  val trackColor = MaterialTheme.colorScheme.surfaceVariant

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Mode Switcher (Focus vs Break)
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(bottom = 24.dp)
    ) {
      FilterChip(
        selected = timerMode == "FOCUS",
        onClick = { if (!isTimerRunning) onModeChanged("FOCUS") },
        label = { Text("Deep Focus (25m)") },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.testTag("mode_chip_focus")
      )

      FilterChip(
        selected = timerMode == "BREAK",
        onClick = { if (!isTimerRunning) onModeChanged("BREAK") },
        label = { Text("Short Break (5m)") },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier.testTag("mode_chip_break")
      )
    }

    // Circular Countdown Canvas
    Box(
      modifier = Modifier
        .size(260.dp)
        .testTag("timer_circular_display"),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(240.dp)) {
        val strokeWidth = 14.dp.toPx()
        // Background track circle
        drawCircle(
          color = trackColor,
          radius = (size.minDimension - strokeWidth) / 2,
          style = Stroke(width = strokeWidth)
        )
        // Progress sweep arc
        drawArc(
          color = primaryColor,
          startAngle = -90f,
          sweepAngle = 360f * animatedProgress,
          useCenter = false,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = formattedTime,
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.testTag("timer_text_display")
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (timerMode == "FOCUS") "Focus Interval" else "Rest & Recharge",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(36.dp))

    // Control Buttons
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = onResetTimer,
        shape = CircleShape,
        modifier = Modifier
          .size(56.dp)
          .testTag("reset_timer_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset Timer",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Button(
        onClick = {
          if (isTimerRunning) onPauseTimer() else onStartTimer()
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isTimerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
          .height(56.dp)
          .width(160.dp)
          .testTag("toggle_timer_button")
      ) {
        Icon(
          imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isTimerRunning) "Pause Timer" else "Start Timer",
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isTimerRunning) "Pause" else "Start Focus",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(36.dp))

    // Sessions today card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Completed Sessions",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Each represents 25 minutes of deep focus",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Text(
          text = "$completedSessionsCount",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("completed_sessions_badge")
        )
      }
    }
  }
}
