package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovaTasksWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val database = AppDatabase.getDatabase(context)
    val upcomingTasks = database.taskDao().getUpcomingTasks(limit = 10)

    provideContent {
      WidgetContent(tasks = upcomingTasks)
    }
  }

  @Composable
  private fun WidgetContent(tasks: List<TaskEntity>) {
    Column(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(Color(0xFF0D121F))
        .cornerRadius(18.dp)
        .padding(12.dp)
    ) {
      // Header
      Row(
        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo and Title
        Row(
          modifier = GlanceModifier.defaultWeight().clickable(actionStartActivity<MainActivity>()),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            provider = ImageProvider(R.drawable.ic_nova_logo),
            contentDescription = "Nova",
            modifier = GlanceModifier.size(20.dp)
          )
          Spacer(modifier = GlanceModifier.width(6.dp))
          Text(
            text = "Nova Tasks",
            style = TextStyle(
              color = ColorProvider(Color.White),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = GlanceModifier.width(6.dp))
          // Count pill
          Box(
            modifier = GlanceModifier
              .background(Color(0xFF1E293B))
              .cornerRadius(10.dp)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${tasks.size}",
              style = TextStyle(
                color = ColorProvider(Color(0xFF94A3B8)),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }

        // Action icons
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Refresh action
          Box(
            modifier = GlanceModifier
              .size(28.dp)
              .background(Color(0xFF1E293B))
              .cornerRadius(14.dp)
              .clickable(actionRunCallback<RefreshWidgetAction>()),
            contentAlignment = Alignment.Center
          ) {
            Image(
              provider = ImageProvider(R.drawable.ic_widget_refresh),
              contentDescription = "Refresh",
              modifier = GlanceModifier.size(16.dp)
            )
          }

          Spacer(modifier = GlanceModifier.width(6.dp))

          // Add task / open app action
          Box(
            modifier = GlanceModifier
              .size(28.dp)
              .background(Color(0xFF2563EB))
              .cornerRadius(14.dp)
              .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
          ) {
            Image(
              provider = ImageProvider(R.drawable.ic_widget_add),
              contentDescription = "Add Task",
              modifier = GlanceModifier.size(16.dp)
            )
          }
        }
      }

      // Divider line
      Box(
        modifier = GlanceModifier
          .fillMaxWidth()
          .height(1.dp)
          .background(Color(0xFF1E293B))
      ) {}

      Spacer(modifier = GlanceModifier.height(8.dp))

      // Content List or Empty State
      if (tasks.isEmpty()) {
        Box(
          modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>()),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 12.dp)
          ) {
            Image(
              provider = ImageProvider(R.drawable.ic_widget_celebrate),
              contentDescription = "All done",
              modifier = GlanceModifier.size(32.dp)
            )
            Spacer(modifier = GlanceModifier.height(6.dp))
            Text(
              text = "All tasks completed!",
              style = TextStyle(
                color = ColorProvider(Color(0xFFE2E8F0)),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
              text = "Tap to create new tasks in Nova",
              style = TextStyle(
                color = ColorProvider(Color(0xFF64748B)),
                fontSize = 11.sp
              )
            )
          }
        }
      } else {
        LazyColumn(
          modifier = GlanceModifier.fillMaxSize()
        ) {
          items(tasks) { task ->
            TaskWidgetItem(task = task)
          }
        }
      }
    }
  }

  @Composable
  private fun TaskWidgetItem(task: TaskEntity) {
    val priorityColor = when (task.priority.uppercase()) {
      "HIGH" -> Color(0xFFEF4444)
      "MEDIUM" -> Color(0xFFF59E0B)
      else -> Color(0xFF10B981)
    }

    val dueDateText = task.dueDateMillis?.let { millis ->
      val now = System.currentTimeMillis()
      val diff = millis - now
      when {
        diff < 0 -> "Overdue"
        diff < 24 * 3600 * 1000L -> "Today"
        diff < 48 * 3600 * 1000L -> "Tomorrow"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(millis))
      }
    }

    Row(
      modifier = GlanceModifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .background(Color(0xFF161E31))
        .cornerRadius(10.dp)
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Toggle completion button
      Box(
        modifier = GlanceModifier
          .size(26.dp)
          .clickable(
            actionRunCallback<ToggleTaskAction>(
              actionParametersOf(ToggleTaskAction.taskIdKey to task.id)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Image(
          provider = ImageProvider(R.drawable.ic_widget_circle),
          contentDescription = "Mark task completed",
          modifier = GlanceModifier.size(20.dp)
        )
      }

      Spacer(modifier = GlanceModifier.width(8.dp))

      // Task information (Title & Meta)
      Column(
        modifier = GlanceModifier
          .defaultWeight()
          .clickable(actionStartActivity<MainActivity>())
      ) {
        Text(
          text = task.title,
          maxLines = 1,
          style = TextStyle(
            color = ColorProvider(Color.White),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = GlanceModifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Priority tag
          Text(
            text = task.priority.lowercase().replaceFirstChar { it.uppercase() },
            style = TextStyle(
              color = ColorProvider(priorityColor),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          )

          // Due date if present
          if (dueDateText != null) {
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
              text = "• $dueDateText",
              style = TextStyle(
                color = ColorProvider(
                  if (dueDateText == "Overdue") Color(0xFFF87171) else Color(0xFF94A3B8)
                ),
                fontSize = 10.sp
              )
            )
          }

          // Category tag
          Spacer(modifier = GlanceModifier.width(6.dp))
          Text(
            text = "• ${task.category.lowercase()}",
            style = TextStyle(
              color = ColorProvider(Color(0xFF64748B)),
              fontSize = 10.sp
            )
          )
        }
      }
    }
  }
}

class ToggleTaskAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    val taskId = parameters[taskIdKey] ?: return
    val database = AppDatabase.getDatabase(context)
    val task = database.taskDao().getTaskById(taskId)
    if (task != null) {
      database.taskDao().updateTaskCompletion(
        id = taskId,
        isCompleted = !task.isCompleted
      )
      NovaTasksWidget().update(context, glanceId)
    }
  }

  companion object {
    val taskIdKey = ActionParameters.Key<Int>("task_id")
  }
}

class RefreshWidgetAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    NovaTasksWidget().update(context, glanceId)
  }
}
