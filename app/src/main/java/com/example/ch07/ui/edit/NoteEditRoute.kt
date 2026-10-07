package com.example.ch07.ui.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch07.MainApplication

// noteId = 0 berarti catatan baru; selain itu, mengedit catatan tersebut
@Composable
fun NoteEditRoute(
    noteId: Int,
    onNavigateBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication

    val viewModel: NoteEditViewModel = viewModel(
        key = "note-edit-$noteId",
        factory = viewModelFactory {
            initializer { NoteEditViewModel(app.container.noteRepository, noteId) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoteEditScreen(
        isNewNote = noteId == 0,
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        // *TODO [T4.5] Teruskan viewModel::onTagsChange.*
        onTagsChange = viewModel::onTagsChange,
        onSave = viewModel::save,
        onNavigateBack = onNavigateBack
    )
}
