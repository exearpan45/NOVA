package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.FocusSession

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val durationMinutes: Int,
  val mode: String, // "FOCUS" or "BREAK"
  val completedAtMillis: Long = System.currentTimeMillis(),
  val tag: String = "General"
) {
  fun toDomain(): FocusSession {
    return FocusSession(
      id = id,
      durationMinutes = durationMinutes,
      mode = mode,
      completedAtMillis = completedAtMillis,
      tag = tag
    )
  }

  companion object {
    fun fromDomain(session: FocusSession): FocusSessionEntity {
      return FocusSessionEntity(
        id = session.id,
        durationMinutes = session.durationMinutes,
        mode = session.mode,
        completedAtMillis = session.completedAtMillis,
        tag = session.tag
      )
    }
  }
}
