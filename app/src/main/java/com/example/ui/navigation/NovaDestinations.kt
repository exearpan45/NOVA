package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class NovaDestination(
  val route: String,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  TASKS(
    route = "tasks",
    title = "Tasks",
    selectedIcon = Icons.Filled.CheckCircle,
    unselectedIcon = Icons.Outlined.CheckCircle
  ),
  NOTES(
    route = "notes",
    title = "Notes",
    selectedIcon = Icons.Filled.Description,
    unselectedIcon = Icons.Outlined.Description
  ),
  FOCUS(
    route = "focus",
    title = "Focus",
    selectedIcon = Icons.Filled.Timer,
    unselectedIcon = Icons.Outlined.Timer
  ),
  INSIGHTS(
    route = "insights",
    title = "Insights",
    selectedIcon = Icons.Filled.Insights,
    unselectedIcon = Icons.Outlined.Insights
  )
}
