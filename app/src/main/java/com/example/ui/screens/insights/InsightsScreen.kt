package com.example.ui.screens.insights

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.FocusSession
import com.example.domain.model.NoteItem
import com.example.domain.model.Priority
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import com.example.ui.theme.CategoryIdeasColor
import com.example.ui.theme.CategoryPersonalColor
import com.example.ui.theme.CategoryStudyColor
import com.example.ui.theme.CategoryUrgentColor
import com.example.ui.theme.CategoryWorkColor

@Composable
fun InsightsScreen(
  tasks: List<TaskItem>,
  notes: List<NoteItem>,
  focusSessions: List<FocusSession>,
  modifier: Modifier = Modifier
) {
  val totalTasks = tasks.size
  val completedTasks = tasks.count { it.isCompleted }
  val pendingTasks = totalTasks - completedTasks
  val completionRate = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0

  val totalFocusMinutes = focusSessions.filter { it.mode == "FOCUS" }.sumOf { it.durationMinutes }
  val totalNotes = notes.size

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
      .testTag("insights_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header banner
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
          Text(
            text = if (completionRate >= 70) "Peak Performance!" else if (completionRate >= 30) "Steady Momentum" else "Kickstart Your Day",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "$completionRate% overall completion rate with $totalFocusMinutes min focused",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
      }
    }

    // 2x2 Metric Cards Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricCard(
        title = "Completed",
        value = "$completedTasks",
        subtitle = "out of $totalTasks total",
        icon = Icons.Default.CheckCircle,
        iconTint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Pending",
        value = "$pendingTasks",
        subtitle = "tasks remaining",
        icon = Icons.Default.PendingActions,
        iconTint = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.weight(1f)
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      MetricCard(
        title = "Deep Focus",
        value = "${totalFocusMinutes}m",
        subtitle = "${focusSessions.size} logged sessions",
        icon = Icons.Default.Timer,
        iconTint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Notes & Ideas",
        value = "$totalNotes",
        subtitle = "${notes.count { it.isPinned }} pinned",
        icon = Icons.Default.Description,
        iconTint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f)
      )
    }

    // Category Breakdown Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Tasks by Category",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        val categories = listOf(
          TaskCategory.WORK to CategoryWorkColor,
          TaskCategory.PERSONAL to CategoryPersonalColor,
          TaskCategory.STUDY to CategoryStudyColor,
          TaskCategory.IDEAS to CategoryIdeasColor,
          TaskCategory.URGENT to CategoryUrgentColor
        )

        categories.forEach { (cat, color) ->
          val count = tasks.count { it.category == cat }
          val percent = if (totalTasks > 0) count.toFloat() / totalTasks.toFloat() else 0f

          Column(modifier = Modifier.padding(vertical = 6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = cat.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$count tasks",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { percent },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = color,
              trackColor = MaterialTheme.colorScheme.surfaceVariant,
              strokeCap = StrokeCap.Round
            )
          }
        }
      }
    }

    // Priority Distribution Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Priority Breakdown",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          PriorityStatItem("High Priority", tasks.count { it.priority == Priority.HIGH }, Color(0xFFEF4444))
          PriorityStatItem("Medium", tasks.count { it.priority == Priority.MEDIUM }, Color(0xFFF59E0B))
          PriorityStatItem("Low Priority", tasks.count { it.priority == Priority.LOW }, Color(0xFF10B981))
        }
      }
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
      )
    }
  }
}

@Composable
private fun PriorityStatItem(
  label: String,
  count: Int,
  indicatorColor: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(indicatorColor)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "$count",
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
