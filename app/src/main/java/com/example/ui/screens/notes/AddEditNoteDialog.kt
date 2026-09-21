package com.example.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.NoteItem
import com.example.domain.model.TaskCategory

@Composable
fun AddEditNoteDialog(
  noteToEdit: NoteItem?,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    content: String,
    category: TaskCategory,
    colorHex: String,
    isPinned: Boolean
  ) -> Unit
) {
  var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
  var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
  var selectedCategory by remember { mutableStateOf(noteToEdit?.category ?: TaskCategory.IDEAS) }
  var isPinned by remember { mutableStateOf(noteToEdit?.isPinned ?: false) }
  var titleError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("add_edit_note_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (noteToEdit != null) "Edit Note" else "New Note",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = { isPinned = !isPinned }) {
          Icon(
            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
            contentDescription = if (isPinned) "Pinned" else "Unpinned",
            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            if (it.isNotBlank()) titleError = false
          },
          label = { Text("Title *") },
          placeholder = { Text("Note title...") },
          isError = titleError,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_note_title"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Content") },
          placeholder = { Text("Write down your ideas, reflections, or meeting notes...") },
          minLines = 4,
          maxLines = 8,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_note_content"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Category",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf(
            TaskCategory.IDEAS,
            TaskCategory.WORK,
            TaskCategory.STUDY,
            TaskCategory.PERSONAL
          ).forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat.label) },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isBlank() && content.isBlank()) {
            titleError = true
          } else {
            onSave(
              title.trim(),
              content.trim(),
              selectedCategory,
              noteToEdit?.colorHex ?: "#6366F1",
              isPinned
            )
          }
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_note_button"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Cancel")
      }
    }
  )
}
