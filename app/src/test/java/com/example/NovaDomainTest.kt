package com.example

import com.example.domain.model.Priority
import com.example.domain.model.SubTask
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem
import com.example.data.local.entity.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NovaDomainTest {

  @Test
  fun `task item conversion to and from entity preserves data`() {
    val original = TaskItem(
      id = 42,
      title = "Complete architecture review",
      description = "Check Room and Compose dependencies",
      priority = Priority.HIGH,
      category = TaskCategory.WORK,
      dueDateMillis = 1770000000000L,
      isCompleted = false,
      subtasks = listOf(
        SubTask("1", "Check KSP compiler", true),
        SubTask("2", "Test DataStore persistence", false)
      )
    )

    val entity = TaskEntity.fromDomain(original)
    assertEquals(42, entity.id)
    assertEquals("Complete architecture review", entity.title)
    assertEquals("HIGH", entity.priority)
    assertEquals("WORK", entity.category)
    assertFalse(entity.isCompleted)

    val restored = entity.toDomain()
    assertEquals(original.id, restored.id)
    assertEquals(original.title, restored.title)
    assertEquals(original.priority, restored.priority)
    assertEquals(original.category, restored.category)
    assertEquals(original.subtasks.size, restored.subtasks.size)
    assertTrue(restored.subtasks[0].isCompleted)
    assertFalse(restored.subtasks[1].isCompleted)
  }

  @Test
  fun `priority enum levels are ordered correctly`() {
    assertTrue(Priority.HIGH.level > Priority.MEDIUM.level)
    assertTrue(Priority.MEDIUM.level > Priority.LOW.level)
  }
}
