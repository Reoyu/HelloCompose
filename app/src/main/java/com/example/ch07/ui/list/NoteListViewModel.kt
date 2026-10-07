package com.example.ch07.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    val recentlyDeleted: NoteEntity? = null,
    // *TODO [T4.6] Tambahkan `availableTags: List<String> = emptyList()` dan*
    //   `selectedTag: String? = null` untuk filter chip.
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val errorMessage: String? = null
) {
    // Turunan: sedang mencari/memfilter atau tidak (menentukan pesan kosong).
    // *TODO [T4.6] Setelah ada selectedTag, sertakan juga `|| selectedTag != null`.*
    val isFiltering: Boolean
        get() = query.isNotBlank() || selectedTag != null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Teks yang sedang diketik (langsung tampil di TextField)
    private val queryText = MutableStateFlow("")
    private val loadError = MutableStateFlow<String?>(null)

    private val recentlyDeleted = MutableStateFlow<NoteEntity?>(null)
    // *TODO [T2.2] Tambahkan `recentlyDeleted = MutableStateFlow<NoteEntity?>(null)`.*
    // *TODO [T4.6] Tambahkan `selectedTag = MutableStateFlow<String?>(null)`.*
    private val selectedTag = MutableStateFlow<String?>(null)

    // Query ke database ditunda 300 ms setelah pengguna berhenti mengetik
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // Query kosong tidak ditunda: daftar penuh tampil segera.
    private val searchResults: Flow<List<NoteEntity>> = queryText
        .debounce { query ->
            if (query.isBlank()) 0L else 300L
        }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllNotes()
            else repository.searchNotes(query)
        }
        .catch { e ->
            loadError.value = e.message ?: "Gagal memuat catatan"
            emit(emptyList())
        }

    private val allNotes: Flow<List<NoteEntity>> =
        repository.getAllNotes()

    private val tagState: Flow<Pair<List<String>, String?>> =
        combine(allNotes, selectedTag) { notes, tag ->
            val availableTags = notes
                .flatMap { it.tags }
                .distinct()
                .sorted()

            val validSelectedTag = if (tag != null && tag !in availableTags) {
                null
            } else {
                tag
            }

            Pair(availableTags, validSelectedTag)
        }

    // UiState DITURUNKAN dari beberapa sumber (pola Chapter 6).
    // *TODO [T2.2][T4.6] Tambahkan sumber baru (recentlyDeleted, selectedTag, dan*
    //   `repository.getAllNotes()` untuk daftar tag) ke combine ini. combine
    //   menerima sampai 5 flow bertipe; gabungkan dua flow dulu jika perlu.
    val uiState: StateFlow<NoteListUiState> = combine(
        searchResults,
        queryText,
        loadError,
        recentlyDeleted,
        tagState
    ) { results, query, error, deleted, tagInfo ->

        val availableTags = tagInfo.first
        val selectedTag = tagInfo.second

        val filteredResults = if (selectedTag == null) {
            results
        } else {
            results.filter { note ->
                selectedTag in note.tags
            }
        }

        NoteListUiState(
            notes = filteredResults,
            query = query,
            recentlyDeleted = deleted,
            availableTags = availableTags,
            selectedTag = selectedTag,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        queryText.value = newQuery
    }

    // *TODO [T4.6] Tambahkan `onTagSelected(tag: String?)`: ketuk tag yang sedang aktif*
    //   *melepas filter; null = "Semua". Tag yang sudah tidak dipakai catatan mana*
    //   *pun harus otomatis dilepas.*
    fun onTagSelected(tag: String?) {
        selectedTag.value = if (selectedTag.value == tag) {
            null
        } else {
            tag
        }
    }

    // *TODO [T2.2] Setelah menghapus, isi recentlyDeleted dengan catatan tersebut.*
    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            recentlyDeleted.value = note
        }
    }

    fun undoDelete() {
        val note = recentlyDeleted.value ?: return

        recentlyDeleted.value = null

        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun onUndoDismissed() {
        recentlyDeleted.value = null
    }

    // *TODO [T2.2] Tambahkan `undoDelete()`: kosongkan recentlyDeleted, lalu masukkan*
    //   *kembali catatan yang sama lewat repository.insertNote (id sama -> REPLACE,*
    //   *sehingga id, pin, dan tag ikut kembali).*
    // *TODO [T2.2] Tambahkan `onUndoDismissed()`: Snackbar hilang tanpa dibatalkan,*
    //   *cukup kosongkan recentlyDeleted.*

    // Menyematkan tidak mengubah updatedAt (bukan perubahan isi catatan)
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}