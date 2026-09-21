package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAtMillis DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE id = :id")
  suspend fun getTaskById(id: Int): TaskEntity?

  @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE WHEN dueDateMillis IS NULL THEN 1 ELSE 0 END, dueDateMillis ASC, CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, createdAtMillis DESC LIMIT :limit")
  suspend fun getUpcomingTasks(limit: Int = 10): List<TaskEntity>

  @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
  suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Int)

  @Query("DELETE FROM tasks WHERE isCompleted = 1")
  suspend fun deleteCompletedTasks()

  @Query("DELETE FROM tasks")
  suspend fun clearAllTasks()
}
