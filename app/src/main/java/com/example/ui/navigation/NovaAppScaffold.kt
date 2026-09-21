package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.NovaTopAppBar
import com.example.ui.screens.focus.FocusTimerScreen
import com.example.ui.screens.insights.InsightsScreen
import com.example.ui.screens.notes.AddEditNoteDialog
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.settings.SettingsDialog
import com.example.ui.screens.tasks.AddEditTaskSheet
import com.example.ui.screens.tasks.TasksScreen
import com.example.viewmodel.NovaUiState
import com.example.viewmodel.NovaViewModel

@Composable
fun NovaAppScaffold(
  viewModel: NovaViewModel,
  uiState: NovaUiState,
  navController: NavHostController = rememberNavController()
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: NovaDestination.TASKS.route

  LaunchedEffect(uiState.snackbarMessage) {
    uiState.snackbarMessage?.let { message ->
      snackbarHostState.showSnackbar(message)
      viewModel.dismissSnackbar()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      val currentDestination = NovaDestination.entries.find { it.route == currentRoute }
      NovaTopAppBar(
        userName = uiState.userPreferences.userName,
        title = currentDestination?.title ?: "Nova",
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onOpenSettings = { viewModel.openSettings() }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("nova_bottom_nav_bar")
      ) {
        NovaDestination.entries.forEach { destination ->
          val isSelected = currentRoute == destination.route
          NavigationBarItem(
            selected = isSelected,
            onClick = {
              if (currentRoute != destination.route) {
                navController.navigate(destination.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            },
            icon = {
              Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = destination.title
              )
            },
            label = { Text(destination.title) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_${destination.route}")
          )
        }
      }
    },
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.testTag("nova_snackbar_host")
      )
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      NavHost(
        navController = navController,
        startDestination = NovaDestination.TASKS.route,
        modifier = Modifier.fillMaxSize()
      ) {
        composable(NovaDestination.TASKS.route) {
          TasksScreen(
            tasks = uiState.tasks,
            allTasks = uiState.allTasks,
            selectedCategory = uiState.selectedCategory,
            selectedSortOrder = uiState.selectedSortOrder,
            activeFilterStatus = uiState.activeFilterStatus,
            onCategorySelected = { viewModel.setCategoryFilter(it) },
            onSortOrderSelected = { viewModel.setSortOrder(it) },
            onStatusFilterSelected = { viewModel.setStatusFilter(it) },
            onToggleComplete = { viewModel.toggleTaskCompletion(it) },
            onToggleSubTask = { task, subId -> viewModel.toggleSubTaskCompletion(task, subId) },
            onEditTask = { viewModel.openEditTask(it) },
            onDeleteTask = { viewModel.deleteTask(it) },
            onDeleteCompletedTasks = { viewModel.deleteCompletedTasks() },
            onAddTask = { viewModel.openAddTask() }
          )
        }

        composable(NovaDestination.NOTES.route) {
          NotesScreen(
            notes = uiState.notes,
            onEditNote = { viewModel.openEditNote(it) },
            onTogglePin = { viewModel.togglePinNote(it) },
            onDeleteNote = { viewModel.deleteNote(it) },
            onAddNote = { viewModel.openAddNote() }
          )
        }

        composable(NovaDestination.FOCUS.route) {
          FocusTimerScreen(
            timerMode = uiState.timerMode,
            timeRemainingSeconds = uiState.timeRemainingSeconds,
            totalTimerSeconds = uiState.totalTimerSeconds,
            isTimerRunning = uiState.isTimerRunning,
            completedSessionsCount = uiState.completedFocusSessionsCount,
            onModeChanged = { viewModel.setTimerMode(it) },
            onStartTimer = { viewModel.startTimer() },
            onPauseTimer = { viewModel.pauseTimer() },
            onResetTimer = { viewModel.resetTimer() }
          )
        }

        composable(NovaDestination.INSIGHTS.route) {
          InsightsScreen(
            tasks = uiState.allTasks,
            notes = uiState.notes,
            focusSessions = uiState.focusSessions
          )
        }
      }
    }
  }

  // Add/Edit Task Sheet
  if (uiState.isTaskSheetOpen) {
    AddEditTaskSheet(
      taskToEdit = uiState.editingTask,
      onDismiss = { viewModel.closeTaskSheet() },
      onSave = { title, desc, priority, category, dueDate, subtasks ->
        viewModel.saveTask(title, desc, priority, category, dueDate, subtasks)
      }
    )
  }

  // Add/Edit Note Dialog
  if (uiState.isNoteDialogOpen) {
    AddEditNoteDialog(
      noteToEdit = uiState.editingNote,
      onDismiss = { viewModel.closeNoteDialog() },
      onSave = { title, content, category, colorHex, isPinned ->
        viewModel.saveNote(title, content, category, colorHex, isPinned)
      }
    )
  }

  // Settings & About Dialog
  if (uiState.isSettingsOpen) {
    SettingsDialog(
      userPreferences = uiState.userPreferences,
      onDismiss = { viewModel.closeSettings() },
      onUpdateUserName = { viewModel.setUserName(it) },
      onUpdateThemeMode = { viewModel.setThemeMode(it) },
      onUpdateFocusMinutes = { viewModel.setFocusMinutes(it) },
      onUpdateBreakMinutes = { viewModel.setBreakMinutes(it) },
      onClearAllData = { viewModel.clearAllData() },
      onReloadSampleData = { viewModel.reloadSampleData() }
    )
  }
}
