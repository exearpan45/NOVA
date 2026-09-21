package com.example.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Priority
import com.example.domain.model.TaskItem
import com.example.ui.theme.CategoryIdeasColor
import com.example.ui.theme.CategoryPersonalColor
import com.example.ui.theme.CategoryStudyColor
import com.example.ui.theme.CategoryUrgentColor
import com.example.ui.theme.CategoryWorkColor
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityLowColor
import com.example.ui.theme.PriorityMediumColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskItemCard(
  task: TaskItem,
  onToggleComplete: () -> Unit,
  onToggleSubTask: (String) -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  val targetAlpha = if (task.isCompleted) 0.6f else 1f
  val contentAlpha by animateFloatAsState(targetValue = targetAlpha, label = "cardAlpha")

  val priorityColor = when (task.priority) {
    Priority.HIGH -> PriorityHighColor
    Priority.MEDIUM -> PriorityMediumColor
    Priority.LOW -> PriorityLowColor
  }

  val categoryColor = when (task.category.name) {
    "WORK" -> CategoryWorkColor
    "PERSONAL" -> CategoryPersonalColor
    "STUDY" -> CategoryStudyColor
    "IDEAS" -> CategoryIdeasColor
    "URGENT" -> CategoryUrgentColor
    else -> CategoryWorkColor
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .alpha(contentAlpha)
      .testTag("task_item_${task.id}"),
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
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Complete checkbox
        Checkbox(
          checked = task.isCompleted,
          onCheckedChange = { onToggleComplete() },
          colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.primary,
            checkmarkColor = Color.White
          ),
          modifier = Modifier.testTag("checkbox_task_${task.id}")
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onEdit() }
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold,
              textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          if (task.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = task.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = if (isExpanded) Int.MAX_VALUE else 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        IconButton(
          onClick = onEdit,
          modifier = Modifier
            .size(36.dp)
            .testTag("edit_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Task",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(36.dp)
            .testTag("delete_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Task",
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Badges: Category, Priority, Due Date
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, start = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Category Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(categoryColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = task.category.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = categoryColor
          )
        }

        // Priority Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(priorityColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = task.priority.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = priorityColor
          )
        }

        // Due date if available
        if (task.dueDateMillis != null) {
          val formattedDueDate = remember(task.dueDateMillis) {
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(task.dueDateMillis))
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = "Due Date",
              modifier = Modifier.size(12.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = formattedDueDate,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Subtasks count indicator
        if (task.subtasks.isNotEmpty()) {
          val completedCount = task.subtasks.count { it.isCompleted }
          Spacer(modifier = Modifier.weight(1f))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clickable { isExpanded = !isExpanded }
              .padding(4.dp)
          ) {
            Text(
              text = "$completedCount/${task.subtasks.size}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.primary
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = "Toggle subtasks",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Subtasks Expanded List
      AnimatedVisibility(visible = isExpanded && task.subtasks.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 36.dp)
        ) {
          task.subtasks.forEach { sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSubTask(sub.id) }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = sub.isCompleted,
                onCheckedChange = { onToggleSubTask(sub.id) },
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = sub.title,
                style = MaterialTheme.typography.bodySmall.copy(
                  textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (sub.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
