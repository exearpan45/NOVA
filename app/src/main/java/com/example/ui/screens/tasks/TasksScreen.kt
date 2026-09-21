package com.example.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import com.example.domain.model.TaskSortOrder
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.EmptyStateView

@Composable
fun TasksScreen(
  tasks: List<TaskItem>,
  allTasks: List<TaskItem>,
  selectedCategory: TaskCategory,
  selectedSortOrder: TaskSortOrder,
  activeFilterStatus: String,
  onCategorySelected: (TaskCategory) -> Unit,
  onSortOrderSelected: (TaskSortOrder) -> Unit,
  onStatusFilterSelected: (String) -> Unit,
  onToggleComplete: (TaskItem) -> Unit,
  onToggleSubTask: (TaskItem, String) -> Unit,
  onEditTask: (TaskItem) -> Unit,
  onDeleteTask: (TaskItem) -> Unit,
  onDeleteCompletedTasks: () -> Unit,
  onAddTask: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showSortMenu by remember { mutableStateOf(false) }

  val totalCount = allTasks.size
  val completedCount = allTasks.count { it.isCompleted }
  val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

  Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Progress summary banner
      if (totalCount > 0) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Today's Momentum",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$completedCount of $totalCount completed (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant,
              strokeCap = StrokeCap.Round
            )
          }
        }
      }

      // Tabs for All, Active, Completed
      val statusTabs = listOf("ALL" to "All", "ACTIVE" to "Active", "COMPLETED" to "Completed")
      val selectedTabIndex = statusTabs.indexOfFirst { it.first == activeFilterStatus }.coerceAtLeast(0)

      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = MaterialTheme.colorScheme.primary
          )
        },
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        statusTabs.forEachIndexed { index, (statusKey, label) ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { onStatusFilterSelected(statusKey) },
            text = {
              Text(
                text = label,
                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
              )
            },
            modifier = Modifier.testTag("tab_status_${statusKey.lowercase()}")
          )
        }
      }

      // Categories and Sort bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(modifier = Modifier.weight(1f)) {
          CategoryChipRow(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
          )
        }

        // Sort icon with popup menu
        Box(modifier = Modifier.padding(end = 12.dp)) {
          IconButton(
            onClick = { showSortMenu = true },
            modifier = Modifier.testTag("sort_tasks_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Sort,
              contentDescription = "Sort Tasks",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Creation Date (Newest)") },
              onClick = {
                onSortOrderSelected(TaskSortOrder.DATE_CREATED)
                showSortMenu = false
              }
            )
            DropdownMenuItem(
              text = { Text("Priority (High to Low)") },
              onClick = {
                onSortOrderSelected(TaskSortOrder.PRIORITY)
                showSortMenu = false
              }
            )
            DropdownMenuItem(
              text = { Text("Due Date") },
              onClick = {
                onSortOrderSelected(TaskSortOrder.DUE_DATE)
                showSortMenu = false
              }
            )
            DropdownMenuItem(
              text = { Text("Alphabetical (A-Z)") },
              onClick = {
                onSortOrderSelected(TaskSortOrder.ALPHABETICAL)
                showSortMenu = false
              }
            )
            if (completedCount > 0) {
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.DeleteSweep,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear completed", color = MaterialTheme.colorScheme.error)
                  }
                },
                onClick = {
                  onDeleteCompletedTasks()
                  showSortMenu = false
                }
              )
            }
          }
        }
      }

      // Tasks List
      if (tasks.isEmpty()) {
        EmptyStateView(
          icon = Icons.Default.CheckCircle,
          title = if (activeFilterStatus == "COMPLETED") "No completed tasks yet" else "No tasks found",
          description = if (activeFilterStatus == "COMPLETED") "Mark items done as you achieve your milestones." else "Capture what needs to get done and stay on track.",
          actionLabel = if (activeFilterStatus != "COMPLETED") "Add New Task" else null,
          onActionClick = if (activeFilterStatus != "COMPLETED") onAddTask else null,
          modifier = Modifier.weight(1f)
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp)
            .testTag("tasks_lazy_column"),
          contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(tasks, key = { it.id }) { task ->
            TaskItemCard(
              task = task,
              onToggleComplete = { onToggleComplete(task) },
              onToggleSubTask = { subId -> onToggleSubTask(task, subId) },
              onEdit = { onEditTask(task) },
              onDelete = { onDeleteTask(task) }
            )
          }
        }
      }
    }

    // FAB
    FloatingActionButton(
      onClick = onAddTask,
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(24.dp)
        .testTag("add_task_fab")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Create Task"
      )
    }
  }
}
