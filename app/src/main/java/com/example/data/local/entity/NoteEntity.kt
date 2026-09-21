package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.NoteItem
import com.example.domain.model.TaskCategory

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val title: String,
  val content: String,
  val category: String = "IDEAS",
  val isPinned: Boolean = false,
  val colorHex: String = "#6366F1",
  val createdAtMillis: Long = System.currentTimeMillis(),
  val updatedAtMillis: Long = System.currentTimeMillis()
) {
  fun toDomain(): NoteItem {
    return NoteItem(
      id = id,
      title = title,
      content = content,
      category = TaskCategory.fromString(category),
      isPinned = isPinned,
      colorHex = colorHex,
      createdAtMillis = createdAtMillis,
      updatedAtMillis = updatedAtMillis
    )
  }

  companion object {
    fun fromDomain(item: NoteItem): NoteEntity {
      return NoteEntity(
        id = item.id,
        title = item.title,
        content = item.content,
        category = item.category.name,
        isPinned = item.isPinned,
        colorHex = item.colorHex,
        createdAtMillis = item.createdAtMillis,
        updatedAtMillis = item.updatedAtMillis
      )
    }
  }
}
