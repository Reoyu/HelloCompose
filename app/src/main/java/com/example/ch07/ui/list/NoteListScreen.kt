package com.example.ch07.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ch07.data.NoteEntity

// Screen murni presentasional: hanya menerima state dan callback.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    uiState: NoteListUiState,
    onQueryChange: (String) -> Unit,
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    // *TODO [T2.3] Tambahkan parameter onUndoDelete: () -> Unit dan onUndoDismissed: () -> Unit.*
    onUndoDelete: () -> Unit,
    onUndoDismissed: () -> Unit,
    // *TODO [T4.7] Tambahkan parameter onTagSelected: (String?) -> Unit.*
    onTagSelected: (String?) -> Unit,
    onTogglePin: (NoteEntity) -> Unit
) {
    // *TODO [T2.3] Buat SnackbarHostState dan pasang di Scaffold (snackbarHost = ...).*
    //   *Gunakan LaunchedEffect(uiState.recentlyDeleted): jika tidak null, tampilkan*
    //   *showSnackbar(message = "Catatan dihapus", actionLabel = "Batalkan").*
    //   *ActionPerformed -> onUndoDelete(); selain itu -> onUndoDismissed().*
    //   *Petunjuk: bungkus callback dengan rememberUpdatedState agar tidak basi.*

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val currentOnUndoDelete = rememberUpdatedState(onUndoDelete)
    val currentOnUndoDismissed = rememberUpdatedState(onUndoDismissed)

    LaunchedEffect(uiState.recentlyDeleted) {
        if (uiState.recentlyDeleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Catatan dihapus",
                actionLabel = "Batalkan"
            )

            if (result == SnackbarResult.ActionPerformed) {
                currentOnUndoDelete.value()
            } else {
                currentOnUndoDismissed.value()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("CatatanKu")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Tambah catatan"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            NoteSearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
            )

            // *TODO [T4.7] Jika uiState.availableTags tidak kosong, tampilkan baris*
            //   *filter chip (LazyRow + FilterChip): "Semua" lalu "#tag" untuk tiap tag;*
            //   *chip terpilih = uiState.selectedTag; ketuk -> onTagSelected(...).*
            if (uiState.availableTags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedTag == null,
                            onClick = {
                                onTagSelected(null)
                            },
                            label = {
                                Text("Semua")
                            }
                        )
                    }

                    items(uiState.availableTags) { tag ->
                        FilterChip(
                            selected = uiState.selectedTag == tag,
                            onClick = {
                                onTagSelected(tag)
                            },
                            label = {
                                Text("#$tag")
                            }
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null ->
                    CenteredMessage(uiState.errorMessage)

                uiState.notes.isEmpty() ->
                    CenteredMessage(emptyMessage(uiState))

                else -> {
                    // *TODO [T3.5] Hitung `val now = remember(uiState.notes) { System.currentTimeMillis() }`*
                    //   *lalu kirim ke NoteCard(now = now, ...).*
                    val now = remember(uiState.notes) {
                        System.currentTimeMillis()
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            uiState.notes,
                            key = { it.id }
                        ) { note ->
                            NoteCard(
                                note = note,
                                now = now,
                                onClick = {
                                    onEditNote(note.id)
                                },
                                onDelete = {
                                    onDeleteNote(note)
                                },
                                onTogglePin = {
                                    onTogglePin(note)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Pesan kosong dibedakan: belum ada catatan sama sekali vs hasil pencarian kosong
// *TODO [T4.7] Tambahkan cabang untuk filter tag: "Tidak ada catatan dengan tag #..."*
private fun emptyMessage(uiState: NoteListUiState): String = when {
    !uiState.isFiltering ->
        "Belum ada catatan. Ketuk + untuk memulai."

    uiState.selectedTag != null ->
        "Tidak ada catatan dengan tag #${uiState.selectedTag}"

    else ->
        "Tidak ada catatan yang cocok dengan \"${uiState.query}\""
}

@Composable
private fun CenteredMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}

// TextField hanya melaporkan teks; debounce dan query ada di ViewModel.
@Composable
private fun NoteSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text("Cari judul atau isi catatan...")
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                    }
                ) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Hapus pencarian"
                    )
                }
            }
        },
        singleLine = true
    )
}