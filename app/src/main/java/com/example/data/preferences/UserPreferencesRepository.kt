package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "nova_user_prefs")

data class UserPreferences(
  val themeMode: String = "system", // "system", "light", "dark"
  val dynamicColor: Boolean = false,
  val userName: String = "Arpan",
  val focusMinutes: Int = 25,
  val breakMinutes: Int = 5,
  val defaultPriority: String = "MEDIUM",
  val firstRunInitialized: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

  private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val USER_NAME = stringPreferencesKey("user_name")
    val FOCUS_MINUTES = intPreferencesKey("focus_minutes")
    val BREAK_MINUTES = intPreferencesKey("break_minutes")
    val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
    val FIRST_RUN_INITIALIZED = booleanPreferencesKey("first_run_initialized")
  }

  val userPreferencesFlow: Flow<UserPreferences> = context.userDataStore.data
    .catch { exception ->
      if (exception is IOException) {
        emit(emptyPreferences())
      } else {
        throw exception
      }
    }
    .map { preferences ->
      UserPreferences(
        themeMode = preferences[Keys.THEME_MODE] ?: "system",
        dynamicColor = preferences[Keys.DYNAMIC_COLOR] ?: false,
        userName = preferences[Keys.USER_NAME] ?: "Arpan",
        focusMinutes = preferences[Keys.FOCUS_MINUTES] ?: 25,
        breakMinutes = preferences[Keys.BREAK_MINUTES] ?: 5,
        defaultPriority = preferences[Keys.DEFAULT_PRIORITY] ?: "MEDIUM",
        firstRunInitialized = preferences[Keys.FIRST_RUN_INITIALIZED] ?: false
      )
    }

  suspend fun setThemeMode(mode: String) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.THEME_MODE] = mode
    }
  }

  suspend fun setDynamicColor(enabled: Boolean) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.DYNAMIC_COLOR] = enabled
    }
  }

  suspend fun setUserName(name: String) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.USER_NAME] = name
    }
  }

  suspend fun setFocusMinutes(minutes: Int) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.FOCUS_MINUTES] = minutes
    }
  }

  suspend fun setBreakMinutes(minutes: Int) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.BREAK_MINUTES] = minutes
    }
  }

  suspend fun setDefaultPriority(priority: String) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.DEFAULT_PRIORITY] = priority
    }
  }

  suspend fun setFirstRunInitialized(initialized: Boolean) {
    context.userDataStore.edit { preferences ->
      preferences[Keys.FIRST_RUN_INITIALIZED] = initialized
    }
  }
}
