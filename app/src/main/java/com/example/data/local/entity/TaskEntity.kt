package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Priority
import com.example.domain.model.SubTask
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val title: String,
  val description: String = "",
  val priority: String = "MEDIUM",
  val category: String = "WORK",
  val dueDateMillis: Long? = null,
  val isCompleted: Boolean = false,
  val subtasksRaw: String = "[]",
  val createdAtMillis: Long = System.currentTimeMillis()
) {
  fun toDomain(): TaskItem {
    val subtaskList = mutableListOf<SubTask>()
    try {
      val jsonArray = JSONArray(subtasksRaw)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        subtaskList.add(
          SubTask(
            id = obj.optString("id", "${System.currentTimeMillis()}_$i"),
            title = obj.optString("title", ""),
            isCompleted = obj.optBoolean("isCompleted", false)
          )
        )
      }
    } catch (_: Exception) {
      // Fallback gracefully on parsing
    }

    return TaskItem(
      id = id,
      title = title,
      description = description,
      priority = Priority.fromString(priority),
      category = TaskCategory.fromString(category),
      dueDateMillis = dueDateMillis,
      isCompleted = isCompleted,
      subtasks = subtaskList,
      createdAtMillis = createdAtMillis
    )
  }

  companion object {
    fun fromDomain(item: TaskItem): TaskEntity {
      val jsonArray = JSONArray()
      for (sub in item.subtasks) {
        val obj = JSONObject()
        obj.put("id", sub.id)
        obj.put("title", sub.title)
        obj.put("isCompleted", sub.isCompleted)
        jsonArray.put(obj)
      }

      return TaskEntity(
        id = item.id,
        title = item.title,
        description = item.description,
        priority = item.priority.name,
        category = item.category.name,
        dueDateMillis = item.dueDateMillis,
        isCompleted = item.isCompleted,
        subtasksRaw = jsonArray.toString(),
        createdAtMillis = item.createdAtMillis
      )
    }
  }
}
