package com.example.data.repository

import com.example.data.local.dao.FocusSessionDao
import com.example.data.local.entity.FocusSessionEntity
import com.example.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FocusRepository(private val focusSessionDao: FocusSessionDao) {

  val allSessions: Flow<List<FocusSession>> = focusSessionDao.getAllSessions().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun logSession(durationMinutes: Int, mode: String, tag: String = "General"): Long {
    val session = FocusSession(
      durationMinutes = durationMinutes,
      mode = mode,
      completedAtMillis = System.currentTimeMillis(),
      tag = tag
    )
    return focusSessionDao.insertSession(FocusSessionEntity.fromDomain(session))
  }

  suspend fun clearAllSessions() {
    focusSessionDao.clearAllSessions()
  }

  suspend fun populateInitialData() {
    // Add 2 initial completed sessions for a realistic stats baseline
    focusSessionDao.insertSession(
      FocusSessionEntity(
        durationMinutes = 25,
        mode = "FOCUS",
        completedAtMillis = System.currentTimeMillis() - 3600000L * 2,
        tag = "Deep Work"
      )
    )
    focusSessionDao.insertSession(
      FocusSessionEntity(
        durationMinutes = 25,
        mode = "FOCUS",
        completedAtMillis = System.currentTimeMillis() - 3600000L * 4,
        tag = "Architecture"
      )
    )
  }
}
