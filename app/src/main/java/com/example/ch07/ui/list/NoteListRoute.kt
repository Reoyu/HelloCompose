package com.example.ch07.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch07.MainApplication

// Route: satu-satunya bagian yang tahu soal ViewModel dan Context
@Composable
fun NoteListRoute(
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication

    val viewModel: NoteListViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                NoteListViewModel(app.container.noteRepository)
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoteListScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onAddNote = onAddNote,
        onEditNote = onEditNote,
        onDeleteNote = viewModel::deleteNote,
        onUndoDelete = viewModel::undoDelete,
        onUndoDismissed = viewModel::onUndoDismissed,
        // *TODO [T4.6] Teruskan viewModel::onTagSelected.*
        onTagSelected = viewModel::onTagSelected,
        onTogglePin = viewModel::togglePin
    )
}