package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.FocusRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TaskRepository
import com.example.domain.model.FocusSession
import com.example.domain.model.NoteItem
import com.example.domain.model.Priority
import com.example.domain.model.SubTask
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import com.example.domain.model.TaskSortOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NovaUiState(
  val tasks: List<TaskItem> = emptyList(),
  val allTasks: List<TaskItem> = emptyList(),
  val notes: List<NoteItem> = emptyList(),
  val focusSessions: List<FocusSession> = emptyList(),
  val userPreferences: UserPreferences = UserPreferences(),
  val searchQuery: String = "",
  val selectedCategory: TaskCategory = TaskCategory.ALL,
  val selectedSortOrder: TaskSortOrder = TaskSortOrder.DATE_CREATED,
  val activeFilterStatus: String = "ALL", // "ALL", "ACTIVE", "COMPLETED"
  // Focus Timer
  val timerMode: String = "FOCUS", // "FOCUS" or "BREAK"
  val timeRemainingSeconds: Int = 25 * 60,
  val totalTimerSeconds: Int = 25 * 60,
  val isTimerRunning: Boolean = false,
  val completedFocusSessionsCount: Int = 0,
  // Sheets and Dialogs
  val editingTask: TaskItem? = null,
  val isTaskSheetOpen: Boolean = false,
  val editingNote: NoteItem? = null,
  val isNoteDialogOpen: Boolean = false,
  val isConfirmClearDataOpen: Boolean = false,
  val isSettingsOpen: Boolean = false,
  val snackbarMessage: String? = null
)

class NovaViewModel(
  private val taskRepository: TaskRepository,
  private val noteRepository: NoteRepository,
  private val focusRepository: FocusRepository,
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _selectedCategory = MutableStateFlow(TaskCategory.ALL)
  private val _selectedSortOrder = MutableStateFlow(TaskSortOrder.DATE_CREATED)
  private val _activeFilterStatus = MutableStateFlow("ALL") // "ALL", "ACTIVE", "COMPLETED"

  // Timer internal state
  private val _timerMode = MutableStateFlow("FOCUS")
  private val _timeRemainingSeconds = MutableStateFlow(25 * 60)
  private val _totalTimerSeconds = MutableStateFlow(25 * 60)
  private val _isTimerRunning = MutableStateFlow(false)

  // Dialog & transient states
  private val _editingTask = MutableStateFlow<TaskItem?>(null)
  private val _isTaskSheetOpen = MutableStateFlow(false)
  private val _editingNote = MutableStateFlow<NoteItem?>(null)
  private val _isNoteDialogOpen = MutableStateFlow(false)
  private val _isConfirmClearDataOpen = MutableStateFlow(false)
  private val _isSettingsOpen = MutableStateFlow(false)
  private val _snackbarMessage = MutableStateFlow<String?>(null)

  private var timerJob: Job? = null

  init {
    viewModelScope.launch {
      userPreferencesRepository.userPreferencesFlow.collect { prefs ->
        if (!prefs.firstRunInitialized) {
          taskRepository.populateInitialData()
          noteRepository.populateInitialData()
          focusRepository.populateInitialData()
          userPreferencesRepository.setFirstRunInitialized(true)
        }
      }
    }
  }

  private data class FilterParams(
    val query: String,
    val category: TaskCategory,
    val sortOrder: TaskSortOrder,
    val statusFilter: String
  )

  private data class TimerState(
    val timerMode: String,
    val timeRemainingSeconds: Int,
    val totalTimerSeconds: Int,
    val isTimerRunning: Boolean
  )

  private data class SheetNoteState(
    val editingTask: TaskItem?,
    val isTaskSheetOpen: Boolean,
    val editingNote: NoteItem?,
    val isNoteDialogOpen: Boolean
  )

  private data class DialogState(
    val editingTask: TaskItem?,
    val isTaskSheetOpen: Boolean,
    val editingNote: NoteItem?,
    val isNoteDialogOpen: Boolean,
    val isSettingsOpen: Boolean,
    val isConfirmClearDataOpen: Boolean,
    val snackbarMessage: String?
  )

  private data class DataSnapshot(
    val tasks: List<TaskItem>,
    val notes: List<NoteItem>,
    val sessions: List<FocusSession>,
    val prefs: UserPreferences
  )

  private val filterParamsFlow = combine(
    _searchQuery,
    _selectedCategory,
    _selectedSortOrder,
    _activeFilterStatus
  ) { query, category, sortOrder, statusFilter ->
    FilterParams(query, category, sortOrder, statusFilter)
  }

  private val timerStateFlow = combine(
    _timerMode,
    _timeRemainingSeconds,
    _totalTimerSeconds,
    _isTimerRunning
  ) { mode, remaining, total, isRunning ->
    TimerState(mode, remaining, total, isRunning)
  }

  private val sheetNoteFlow = combine(
    _editingTask,
    _isTaskSheetOpen,
    _editingNote,
    _isNoteDialogOpen
  ) { task, isTaskOpen, note, isNoteOpen ->
    SheetNoteState(task, isTaskOpen, note, isNoteOpen)
  }

  private val dialogStateFlow = combine(
    sheetNoteFlow,
    _isSettingsOpen,
    _isConfirmClearDataOpen,
    _snackbarMessage
  ) { sheetNote, isSettings, isClear, snackbar ->
    DialogState(
      editingTask = sheetNote.editingTask,
      isTaskSheetOpen = sheetNote.isTaskSheetOpen,
      editingNote = sheetNote.editingNote,
      isNoteDialogOpen = sheetNote.isNoteDialogOpen,
      isSettingsOpen = isSettings,
      isConfirmClearDataOpen = isClear,
      snackbarMessage = snackbar
    )
  }

  private val dataSnapshotFlow = combine(
    taskRepository.allTasks,
    noteRepository.allNotes,
    focusRepository.allSessions,
    userPreferencesRepository.userPreferencesFlow
  ) { tasks, notes, sessions, prefs ->
    DataSnapshot(tasks, notes, sessions, prefs)
  }

  // Combined Reactive UI State
  val uiState: StateFlow<NovaUiState> = combine(
    dataSnapshotFlow,
    filterParamsFlow,
    timerStateFlow,
    dialogStateFlow
  ) { data, filters, timer, dialogs ->
    val tasks = data.tasks
    val notes = data.notes
    val sessions = data.sessions
    val prefs = data.prefs

    val query = filters.query
    val category = filters.category
    val sortOrder = filters.sortOrder
    val statusFilter = filters.statusFilter

    // Filter tasks
    var filteredTasks = tasks.filter { task ->
      val matchesQuery = query.isBlank() ||
          task.title.contains(query, ignoreCase = true) ||
          task.description.contains(query, ignoreCase = true)
      val matchesCategory = category == TaskCategory.ALL || task.category == category
      val matchesStatus = when (statusFilter) {
        "ACTIVE" -> !task.isCompleted
        "COMPLETED" -> task.isCompleted
        else -> true
      }
      matchesQuery && matchesCategory && matchesStatus
    }

    // Sort tasks
    filteredTasks = when (sortOrder) {
      TaskSortOrder.DUE_DATE -> filteredTasks.sortedWith(
        compareBy<TaskItem> { it.dueDateMillis ?: Long.MAX_VALUE }.thenBy { it.isCompleted }
      )
      TaskSortOrder.PRIORITY -> filteredTasks.sortedWith(
        compareByDescending<TaskItem> { it.priority.level }.thenBy { it.isCompleted }
      )
      TaskSortOrder.ALPHABETICAL -> filteredTasks.sortedWith(
        compareBy<TaskItem> { it.title.lowercase() }.thenBy { it.isCompleted }
      )
      TaskSortOrder.DATE_CREATED -> filteredTasks.sortedByDescending { it.createdAtMillis }
    }

    // Filter notes by search
    val filteredNotes = if (query.isBlank()) {
      notes
    } else {
      notes.filter {
        it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
      }
    }

    NovaUiState(
      tasks = filteredTasks,
      allTasks = tasks,
      notes = filteredNotes,
      focusSessions = sessions,
      userPreferences = prefs,
      searchQuery = query,
      selectedCategory = category,
      selectedSortOrder = sortOrder,
      activeFilterStatus = statusFilter,
      timerMode = timer.timerMode,
      timeRemainingSeconds = timer.timeRemainingSeconds,
      totalTimerSeconds = timer.totalTimerSeconds,
      isTimerRunning = timer.isTimerRunning,
      completedFocusSessionsCount = sessions.count { it.mode == "FOCUS" },
      editingTask = dialogs.editingTask,
      isTaskSheetOpen = dialogs.isTaskSheetOpen,
      editingNote = dialogs.editingNote,
      isNoteDialogOpen = dialogs.isNoteDialogOpen,
      isConfirmClearDataOpen = dialogs.isConfirmClearDataOpen,
      isSettingsOpen = dialogs.isSettingsOpen,
      snackbarMessage = dialogs.snackbarMessage
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = NovaUiState()
  )

  // Search & Filters
  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCategoryFilter(category: TaskCategory) {
    _selectedCategory.value = category
  }

  fun setSortOrder(order: TaskSortOrder) {
    _selectedSortOrder.value = order
  }

  fun setStatusFilter(status: String) {
    _activeFilterStatus.value = status
  }

  // Task Actions
  fun openAddTask() {
    _editingTask.value = null
    _isTaskSheetOpen.value = true
  }

  fun openEditTask(task: TaskItem) {
    _editingTask.value = task
    _isTaskSheetOpen.value = true
  }

  fun closeTaskSheet() {
    _isTaskSheetOpen.value = false
    _editingTask.value = null
  }

  fun saveTask(
    title: String,
    description: String,
    priority: Priority,
    category: TaskCategory,
    dueDateMillis: Long?,
    subtasks: List<SubTask>
  ) {
    if (title.isBlank()) return

    viewModelScope.launch {
      val current = _editingTask.value
      if (current != null) {
        val updated = current.copy(
          title = title.trim(),
          description = description.trim(),
          priority = priority,
          category = category,
          dueDateMillis = dueDateMillis,
          subtasks = subtasks
        )
        taskRepository.updateTask(updated)
        showSnackbar("Task updated")
      } else {
        val newTask = TaskItem(
          title = title.trim(),
          description = description.trim(),
          priority = priority,
          category = category,
          dueDateMillis = dueDateMillis,
          subtasks = subtasks
        )
        taskRepository.insertTask(newTask)
        showSnackbar("Task created")
      }
      closeTaskSheet()
    }
  }

  fun toggleTaskCompletion(task: TaskItem) {
    viewModelScope.launch {
      taskRepository.toggleTaskCompletion(task)
    }
  }

  fun toggleSubTaskCompletion(task: TaskItem, subTaskId: String) {
    viewModelScope.launch {
      taskRepository.toggleSubTaskCompletion(task, subTaskId)
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      taskRepository.deleteTask(task)
      showSnackbar("Task deleted")
    }
  }

  fun deleteCompletedTasks() {
    viewModelScope.launch {
      taskRepository.deleteCompletedTasks()
      showSnackbar("Completed tasks cleaned")
    }
  }

  // Note Actions
  fun openAddNote() {
    _editingNote.value = null
    _isNoteDialogOpen.value = true
  }

  fun openEditNote(note: NoteItem) {
    _editingNote.value = note
    _isNoteDialogOpen.value = true
  }

  fun closeNoteDialog() {
    _isNoteDialogOpen.value = false
    _editingNote.value = null
  }

  fun saveNote(
    title: String,
    content: String,
    category: TaskCategory,
    colorHex: String,
    isPinned: Boolean
  ) {
    if (title.isBlank() && content.isBlank()) return

    viewModelScope.launch {
      val current = _editingNote.value
      if (current != null) {
        val updated = current.copy(
          title = title.trim().ifBlank { "Untitled Note" },
          content = content.trim(),
          category = category,
          colorHex = colorHex,
          isPinned = isPinned,
          updatedAtMillis = System.currentTimeMillis()
        )
        noteRepository.updateNote(updated)
        showSnackbar("Note updated")
      } else {
        val newNote = NoteItem(
          title = title.trim().ifBlank { "Untitled Note" },
          content = content.trim(),
          category = category,
          colorHex = colorHex,
          isPinned = isPinned
        )
        noteRepository.insertNote(newNote)
        showSnackbar("Note saved")
      }
      closeNoteDialog()
    }
  }

  fun togglePinNote(note: NoteItem) {
    viewModelScope.launch {
      noteRepository.togglePin(note)
    }
  }

  fun deleteNote(note: NoteItem) {
    viewModelScope.launch {
      noteRepository.deleteNote(note)
      showSnackbar("Note deleted")
    }
  }

  // Focus Timer Actions
  fun setTimerMode(mode: String) {
    pauseTimer()
    _timerMode.value = mode
    val minutes = if (mode == "FOCUS") {
      uiState.value.userPreferences.focusMinutes
    } else {
      uiState.value.userPreferences.breakMinutes
    }
    _totalTimerSeconds.value = minutes * 60
    _timeRemainingSeconds.value = minutes * 60
  }

  fun startTimer() {
    if (_isTimerRunning.value) return
    if (_timeRemainingSeconds.value <= 0) {
      resetTimer()
    }
    _isTimerRunning.value = true

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_isTimerRunning.value && _timeRemainingSeconds.value > 0) {
        delay(1000L)
        if (!_isTimerRunning.value) break
        val remaining = _timeRemainingSeconds.value - 1
        _timeRemainingSeconds.value = remaining
        if (remaining <= 0) {
          onTimerFinished()
          break
        }
      }
    }
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
    timerJob = null
  }

  fun resetTimer() {
    pauseTimer()
    val minutes = if (_timerMode.value == "FOCUS") {
      uiState.value.userPreferences.focusMinutes
    } else {
      uiState.value.userPreferences.breakMinutes
    }
    _totalTimerSeconds.value = minutes * 60
    _timeRemainingSeconds.value = minutes * 60
  }

  private fun onTimerFinished() {
    pauseTimer()
    viewModelScope.launch {
      val mode = _timerMode.value
      val duration = if (mode == "FOCUS") {
        uiState.value.userPreferences.focusMinutes
      } else {
        uiState.value.userPreferences.breakMinutes
      }

      focusRepository.logSession(
        durationMinutes = duration,
        mode = mode,
        tag = if (mode == "FOCUS") "Deep Work" else "Rest"
      )

      showSnackbar(if (mode == "FOCUS") "Focus session completed! Great job!" else "Break complete! Ready to focus?")
      // Switch mode automatically
      if (mode == "FOCUS") {
        setTimerMode("BREAK")
      } else {
        setTimerMode("FOCUS")
      }
    }
  }

  // Preferences & Settings Actions
  fun openSettings() {
    _isSettingsOpen.value = true
  }

  fun closeSettings() {
    _isSettingsOpen.value = false
  }

  fun setThemeMode(mode: String) {
    viewModelScope.launch {
      userPreferencesRepository.setThemeMode(mode)
    }
  }

  fun setDynamicColor(enabled: Boolean) {
    viewModelScope.launch {
      userPreferencesRepository.setDynamicColor(enabled)
    }
  }

  fun setUserName(name: String) {
    viewModelScope.launch {
      userPreferencesRepository.setUserName(name.trim())
      showSnackbar("User name updated")
    }
  }

  fun setFocusMinutes(minutes: Int) {
    viewModelScope.launch {
      userPreferencesRepository.setFocusMinutes(minutes)
      if (_timerMode.value == "FOCUS" && !_isTimerRunning.value) {
        _totalTimerSeconds.value = minutes * 60
        _timeRemainingSeconds.value = minutes * 60
      }
    }
  }

  fun setBreakMinutes(minutes: Int) {
    viewModelScope.launch {
      userPreferencesRepository.setBreakMinutes(minutes)
      if (_timerMode.value == "BREAK" && !_isTimerRunning.value) {
        _totalTimerSeconds.value = minutes * 60
        _timeRemainingSeconds.value = minutes * 60
      }
    }
  }

  fun showConfirmClearData(show: Boolean) {
    _isConfirmClearDataOpen.value = show
  }

  fun clearAllData() {
    viewModelScope.launch {
      taskRepository.clearAllTasks()
      noteRepository.clearAllNotes()
      focusRepository.clearAllSessions()
      showConfirmClearData(false)
      showSnackbar("All user data cleared")
    }
  }

  fun reloadSampleData() {
    viewModelScope.launch {
      taskRepository.populateInitialData()
      noteRepository.populateInitialData()
      focusRepository.populateInitialData()
      showSnackbar("Sample workspace loaded")
    }
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  fun dismissSnackbar() {
    _snackbarMessage.value = null
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
