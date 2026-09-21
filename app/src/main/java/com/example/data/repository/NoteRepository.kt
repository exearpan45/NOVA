package com.example.data.repository

import com.example.data.local.dao.NoteDao
import com.example.data.local.entity.NoteEntity
import com.example.domain.model.NoteItem
import com.example.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {

  val allNotes: Flow<List<NoteItem>> = noteDao.getAllNotes().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun insertNote(note: NoteItem): Long {
    return noteDao.insertNote(NoteEntity.fromDomain(note))
  }

  suspend fun updateNote(note: NoteItem) {
    val updated = note.copy(updatedAtMillis = System.currentTimeMillis())
    noteDao.updateNote(NoteEntity.fromDomain(updated))
  }

  suspend fun togglePin(note: NoteItem) {
    val updated = note.copy(
      isPinned = !note.isPinned,
      updatedAtMillis = System.currentTimeMillis()
    )
    noteDao.updateNote(NoteEntity.fromDomain(updated))
  }

  suspend fun deleteNote(note: NoteItem) {
    noteDao.deleteNote(NoteEntity.fromDomain(note))
  }

  suspend fun deleteNoteById(id: Int) {
    noteDao.deleteNoteById(id)
  }

  suspend fun clearAllNotes() {
    noteDao.clearAllNotes()
  }

  suspend fun populateInitialData() {
    val sampleNotes = listOf(
      NoteItem(
        title = "Nova Architecture Notes",
        content = "Built on official native Android standards:\n• Jetpack Compose & Material 3\n• Room Database with reactive Kotlin Flow\n• Jetpack DataStore Preferences\n• StateFlow and Unidirectional Data Flow\n• Native Android SplashScreen API\n• Designed by Arpan Goswami",
        category = TaskCategory.IDEAS,
        isPinned = true,
        colorHex = "#6366F1"
      ),
      NoteItem(
        title = "Focus & Deep Work Principles",
        content = "1. Single-tasking beats multi-tasking.\n2. Work in 25-minute Pomodoro intervals.\n3. Take 5-minute cognitive breaks.\n4. Log daily focus sessions to track momentum.",
        category = TaskCategory.STUDY,
        isPinned = false,
        colorHex = "#0EA5E9"
      ),
      NoteItem(
        title = "Project Ideas & Milestones",
        content = "Explore offline-first sync pipelines, local telemetry dashboards, and minimalist typography refinements.",
        category = TaskCategory.WORK,
        isPinned = false,
        colorHex = "#8B5CF6"
      )
    )
    noteDao.insertNotes(sampleNotes.map { NoteEntity.fromDomain(it) })
  }
}
