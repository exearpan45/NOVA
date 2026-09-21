package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.TaskEntity
import com.example.widget.NovaTasksWidget
import com.example.widget.NovaTasksWidgetReceiver
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NovaTasksWidgetTest {

  private lateinit var context: Context
  private lateinit var database: AppDatabase

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testWidgetReceiverInstantiation() {
    val receiver = NovaTasksWidgetReceiver()
    assertNotNull(receiver.glanceAppWidget)
    assertTrue(receiver.glanceAppWidget is NovaTasksWidget)
  }

  @Test
  fun testWidgetResources() {
    val widgetName = context.getString(R.string.widget_name)
    val widgetDesc = context.getString(R.string.widget_description)
    assertEquals("Nova Upcoming Tasks", widgetName)
    assertTrue(widgetDesc.isNotEmpty())
  }

  @Test
  fun testUpcomingTasksQueryAndToggle() = runBlocking {
    val taskDao = database.taskDao()

    val task1 = TaskEntity(
      id = 1,
      title = "Task One",
      isCompleted = false,
      dueDateMillis = System.currentTimeMillis() + 3600000L
    )
    val task2 = TaskEntity(
      id = 2,
      title = "Completed Task",
      isCompleted = true
    )
    val task3 = TaskEntity(
      id = 3,
      title = "Task Three",
      isCompleted = false,
      dueDateMillis = System.currentTimeMillis() + 7200000L
    )

    taskDao.insertTask(task1)
    taskDao.insertTask(task2)
    taskDao.insertTask(task3)

    // Verify upcoming tasks query only returns uncompleted tasks
    val upcoming = taskDao.getUpcomingTasks(limit = 10)
    assertEquals(2, upcoming.size)
    assertEquals("Task One", upcoming[0].title)
    assertEquals("Task Three", upcoming[1].title)

    // Toggle task 1 completion
    taskDao.updateTaskCompletion(id = 1, isCompleted = true)

    val updatedUpcoming = taskDao.getUpcomingTasks(limit = 10)
    assertEquals(1, updatedUpcoming.size)
    assertEquals("Task Three", updatedUpcoming[0].title)

    val updatedTask1 = taskDao.getTaskById(1)
    assertNotNull(updatedTask1)
    assertTrue(updatedTask1!!.isCompleted)
  }
}
