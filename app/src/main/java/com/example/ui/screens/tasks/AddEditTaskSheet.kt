package com.example.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.Priority
import com.example.domain.model.SubTask
import com.example.domain.model.TaskCategory
import com.example.domain.model.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
  taskToEdit: TaskItem?,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    description: String,
    priority: Priority,
    category: TaskCategory,
    dueDateMillis: Long?,
    subtasks: List<SubTask>
  ) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
  var selectedPriority by remember { mutableStateOf(taskToEdit?.priority ?: Priority.MEDIUM) }
  var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: TaskCategory.WORK) }
  var titleError by remember { mutableStateOf(false) }

  // Subtasks list
  val subtaskList = remember {
    mutableStateListOf<SubTask>().apply {
      if (taskToEdit != null) {
        addAll(taskToEdit.subtasks)
      }
    }
  }
  var newSubtaskText by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = Modifier.testTag("add_edit_task_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .navigationBarsPadding()
        .imePadding()
    ) {
      Text(
        text = if (taskToEdit != null) "Edit Task" else "New Task",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Title input
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (it.isNotBlank()) titleError = false
        },
        label = { Text("Task Title *") },
        placeholder = { Text("e.g., Finalize project roadmap") },
        isError = titleError,
        supportingText = {
          if (titleError) Text("Title is required")
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_task_title"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Description input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Notes / Details (optional)") },
        placeholder = { Text("Add key objectives, links, or context...") },
        minLines = 2,
        maxLines = 4,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_task_description"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Priority Selector
      Text(
        text = "Priority",
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Priority.entries.forEach { priority ->
          val isSelected = selectedPriority == priority
          FilterChip(
            selected = isSelected,
            onClick = { selectedPriority = priority },
            label = { Text(priority.label) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("priority_chip_${priority.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category Selector
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        listOf(
          TaskCategory.WORK,
          TaskCategory.PERSONAL,
          TaskCategory.STUDY,
          TaskCategory.IDEAS,
          TaskCategory.URGENT
        ).forEach { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(cat.label) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Subtasks Section
      Text(
        text = "Subtasks / Checklist",
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Existing subtasks
      subtaskList.forEachIndexed { index, sub ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "• ${sub.title}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
          )
          IconButton(
            onClick = { subtaskList.removeAt(index) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove subtask",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Add subtask input
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = newSubtaskText,
          onValueChange = { newSubtaskText = it },
          placeholder = { Text("Add subtask item...") },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("input_new_subtask"),
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            if (newSubtaskText.isNotBlank()) {
              subtaskList.add(
                SubTask(
                  id = "${System.currentTimeMillis()}_${subtaskList.size}",
                  title = newSubtaskText.trim(),
                  isCompleted = false
                )
              )
              newSubtaskText = ""
            }
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("button_add_subtask")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add subtask")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action buttons (Save, Cancel)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier
            .weight(1f)
            .testTag("cancel_task_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            if (title.isBlank()) {
              titleError = true
            } else {
              onSave(
                title.trim(),
                description.trim(),
                selectedPriority,
                selectedCategory,
                taskToEdit?.dueDateMillis,
                subtaskList.toList()
              )
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("save_task_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Text(if (taskToEdit != null) "Update" else "Create")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
