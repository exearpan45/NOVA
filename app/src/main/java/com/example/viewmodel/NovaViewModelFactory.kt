package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.FocusRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TaskRepository

class NovaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NovaViewModel::class.java)) {
      val database = AppDatabase.getDatabase(context)
      val taskRepo = TaskRepository(database.taskDao())
      val noteRepo = NoteRepository(database.noteDao())
      val focusRepo = FocusRepository(database.focusSessionDao())
      val userPrefsRepo = UserPreferencesRepository(context)

      return NovaViewModel(
        taskRepository = taskRepo,
        noteRepository = noteRepo,
        focusRepository = focusRepo,
        userPreferencesRepository = userPrefsRepo
      ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
