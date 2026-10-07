package com.example.ch07.testutil

import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Pengganti Room untuk pengujian: cukup karena ViewModel hanya tahu interface
// NoteRepository (bukti manfaat pola Repository dari Chapter 6).
class FakeNoteRepository(initial: List<NoteEntity> = emptyList()) : NoteRepository {

    private val notes = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0) + 1

    val current: List<NoteEntity> get() = notes.value

    private fun ordered(list: List<NoteEntity>) =
        list.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.createdAt })

    override fun getAllNotes(): Flow<List<NoteEntity>> = notes.map { ordered(it) }

    override fun searchNotes(query: String): Flow<List<NoteEntity>> = notes.map { list ->
        ordered(list.filter { it.title.contains(query, true) || it.content.contains(query, true) })
    }

    override suspend fun getNoteById(id: Int): NoteEntity? = notes.value.firstOrNull { it.id == id }

    override suspend fun insertNote(note: NoteEntity): Long {
        val id = if (note.id == 0) nextId++ else note.id
        notes.update { list -> list.filterNot { it.id == id } + note.copy(id = id) }
        return id.toLong()
    }

    override suspend fun updateNote(note: NoteEntity) {
        notes.update { list -> list.map { if (it.id == note.id) note else it } }
    }

    override suspend fun deleteNote(note: NoteEntity) {
        notes.update { list -> list.filterNot { it.id == note.id } }
    }
}
