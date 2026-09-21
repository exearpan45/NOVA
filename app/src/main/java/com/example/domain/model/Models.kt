package com.example.domain.model

enum class Priority(val label: String, val level: Int) {
  LOW("Low", 1),
  MEDIUM("Medium", 2),
  HIGH("High", 3);

  companion object {
    fun fromString(value: String): Priority {
      return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
    }
  }
}

enum class TaskCategory(val label: String) {
  ALL("All"),
  WORK("Work"),
  PERSONAL("Personal"),
  STUDY("Study"),
  IDEAS("Ideas"),
  URGENT("Urgent");

  companion object {
    fun fromString(value: String): TaskCategory {
      return entries.find { it.name.equals(value, ignoreCase = true) } ?: WORK
    }
  }
}

data class SubTask(
  val id: String,
  val title: String,
  val isCompleted: Boolean = false
)

data class TaskItem(
  val id: Int = 0,
  val title: String,
  val description: String = "",
  val priority: Priority = Priority.MEDIUM,
  val category: TaskCategory = TaskCategory.WORK,
  val dueDateMillis: Long? = null,
  val isCompleted: Boolean = false,
  val subtasks: List<SubTask> = emptyList(),
  val createdAtMillis: Long = System.currentTimeMillis()
)

data class NoteItem(
  val id: Int = 0,
  val title: String,
  val content: String,
  val category: TaskCategory = TaskCategory.IDEAS,
  val isPinned: Boolean = false,
  val colorHex: String = "#6366F1",
  val createdAtMillis: Long = System.currentTimeMillis(),
  val updatedAtMillis: Long = System.currentTimeMillis()
)

data class FocusSession(
  val id: Int = 0,
  val durationMinutes: Int,
  val mode: String, // "FOCUS" or "BREAK"
  val completedAtMillis: Long = System.currentTimeMillis(),
  val tag: String = "General"
)

enum class TaskSortOrder {
  DUE_DATE,
  PRIORITY,
  ALPHABETICAL,
  DATE_CREATED
}
