package com.example.ch07.data

import kotlinx.coroutines.flow.Flow

// ViewModel tidak boleh berbicara langsung ke DAO. Repository menyembunyikan
// detail penyimpanan, dan bisa diganti Fake saat pengujian.
interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: Int): NoteEntity?
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
}

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<NoteEntity>> = dao.getAllNotes()

    override fun searchNotes(query: String): Flow<List<NoteEntity>> =
        dao.searchNotes(query)

    override suspend fun getNoteById(id: Int): NoteEntity? = dao.getNoteById(id)

    override suspend fun insertNote(note: NoteEntity): Long = dao.insertNote(note)

    override suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)

    override suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)
}
