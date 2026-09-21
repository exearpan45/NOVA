package com.example.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.NoteItem
import com.example.ui.components.EmptyStateView

@Composable
fun NotesScreen(
  notes: List<NoteItem>,
  onEditNote: (NoteItem) -> Unit,
  onTogglePin: (NoteItem) -> Unit,
  onDeleteNote: (NoteItem) -> Unit,
  onAddNote: () -> Unit,
  modifier: Modifier = Modifier
) {
  val pinnedNotes = notes.filter { it.isPinned }
  val otherNotes = notes.filter { !it.isPinned }

  Box(modifier = modifier.fillMaxSize()) {
    if (notes.isEmpty()) {
      EmptyStateView(
        icon = Icons.Default.Description,
        title = "No notes yet",
        description = "Capture quick thoughts, meeting notes, code snippets, or ideas.",
        actionLabel = "Create Note",
        onActionClick = onAddNote,
        modifier = Modifier.align(Alignment.Center)
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
          .testTag("notes_lazy_column"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (pinnedNotes.isNotEmpty()) {
          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 6.dp)
              )
              Text(
                text = "Pinned",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          items(pinnedNotes, key = { it.id }) { note ->
            NoteCard(
              note = note,
              onEdit = { onEditNote(note) },
              onTogglePin = { onTogglePin(note) },
              onDelete = { onDeleteNote(note) }
            )
          }

          if (otherNotes.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Other Notes",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
              )
            }
          }
        }

        items(otherNotes, key = { it.id }) { note ->
          NoteCard(
            note = note,
            onEdit = { onEditNote(note) },
            onTogglePin = { onTogglePin(note) },
            onDelete = { onDeleteNote(note) }
          )
        }
      }
    }

    FloatingActionButton(
      onClick = onAddNote,
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(24.dp)
        .testTag("add_note_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create Note")
    }
  }
}
