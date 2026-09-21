package com.example.data.repository

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.TaskEntity
import com.example.domain.model.Priority
import com.example.domain.model.SubTask
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import com.example.widget.NovaTasksWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
  private val taskDao: TaskDao,
  private val context: Context? = null
) {

  private suspend fun notifyWidgetUpdate() {
    context?.let { ctx ->
      try {
        NovaTasksWidget().updateAll(ctx)
      } catch (_: Exception) {
        // Safe fallback in test or headless environments
      }
    }
  }

  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun insertTask(task: TaskItem): Long {
    val id = taskDao.insertTask(TaskEntity.fromDomain(task))
    notifyWidgetUpdate()
    return id
  }

  suspend fun updateTask(task: TaskItem) {
    taskDao.updateTask(TaskEntity.fromDomain(task))
    notifyWidgetUpdate()
  }

  suspend fun deleteTask(task: TaskItem) {
    taskDao.deleteTask(TaskEntity.fromDomain(task))
    notifyWidgetUpdate()
  }

  suspend fun deleteTaskById(id: Int) {
    taskDao.deleteTaskById(id)
    notifyWidgetUpdate()
  }

  suspend fun deleteCompletedTasks() {
    taskDao.deleteCompletedTasks()
    notifyWidgetUpdate()
  }

  suspend fun clearAllTasks() {
    taskDao.clearAllTasks()
    notifyWidgetUpdate()
  }

  suspend fun toggleTaskCompletion(task: TaskItem) {
    val updated = task.copy(isCompleted = !task.isCompleted)
    taskDao.updateTask(TaskEntity.fromDomain(updated))
    notifyWidgetUpdate()
  }

  suspend fun toggleSubTaskCompletion(task: TaskItem, subTaskId: String) {
    val updatedSubtasks = task.subtasks.map {
      if (it.id == subTaskId) it.copy(isCompleted = !it.isCompleted) else it
    }
    val allSubtasksDone = updatedSubtasks.isNotEmpty() && updatedSubtasks.all { it.isCompleted }
    val updatedTask = task.copy(
      subtasks = updatedSubtasks,
      isCompleted = if (allSubtasksDone) true else task.isCompleted
    )
    taskDao.updateTask(TaskEntity.fromDomain(updatedTask))
    notifyWidgetUpdate()
  }

  suspend fun populateInitialData() {
    val sampleTasks = listOf(
      TaskItem(
        title = "Welcome to Nova!",
        description = "Nova is your high-performance native Android productivity workspace. Tap the checkbox to complete tasks.",
        priority = Priority.HIGH,
        category = TaskCategory.WORK,
        isCompleted = false,
        subtasks = listOf(
          SubTask("1", "Explore task management", true),
          SubTask("2", "Create your first quick note", false),
          SubTask("3", "Run a 25-minute focus session", false)
        )
      ),
      TaskItem(
        title = "Design App Theme & Layout",
        description = "Experience Material 3 with Dynamic Color and Edge-to-Edge responsiveness.",
        priority = Priority.MEDIUM,
        category = TaskCategory.STUDY,
        isCompleted = true,
        subtasks = listOf(
          SubTask("4", "Configure custom Nova adaptive icon", true),
          SubTask("5", "Test in dark and light modes", true)
        )
      ),
      TaskItem(
        title = "Review Q3 Productivity Goals",
        description = "Break down milestones into actionable focus blocks and track daily stats.",
        priority = Priority.HIGH,
        category = TaskCategory.WORK,
        dueDateMillis = System.currentTimeMillis() + 86400000L * 2,
        isCompleted = false
      ),
      TaskItem(
        title = "Weekend Reading & Research",
        description = "Read latest publications on modern Kotlin concurrency and reactive state flows.",
        priority = Priority.LOW,
        category = TaskCategory.PERSONAL,
        dueDateMillis = System.currentTimeMillis() + 86400000L * 5,
        isCompleted = false
      )
    )
    taskDao.insertTasks(sampleTasks.map { TaskEntity.fromDomain(it) })
    notifyWidgetUpdate()
  }
}
