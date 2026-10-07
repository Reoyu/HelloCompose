package com.example.ch07.ui.edit

import com.example.ch07.data.NoteEntity
import com.example.ch07.testutil.FakeNoteRepository
import com.example.ch07.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Tes perilaku DASAR (sudah hijau sejak awal). Setelah Tugas 3 dan 4, tambahkan
// pemeriksaan updatedAt dan tags (contohnya ada di praktikum/ch07).
@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun `judul kosong ditolak dan tidak menyimpan apa pun`() = runTest {
        val repo = FakeNoteRepository()
        val vm = NoteEditViewModel(repo)

        vm.onTitleChange("   ")
        vm.save(); runCurrent()

        assertEquals("Judul tidak boleh kosong", vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isSaved)
        assertTrue(repo.current.isEmpty())
    }

    @Test fun `catatan baru - createdAt dari clock dan teks dirapikan`() = runTest {
        val repo = FakeNoteRepository()
        val vm = NoteEditViewModel(repo, noteId = 0, clock = { 5_000L })

        vm.onTitleChange("  Rencana  ")
        vm.onContentChange(" isi ")
        vm.save(); runCurrent()

        val saved = repo.current.single()
        assertEquals("Rencana", saved.title)
        assertEquals("isi", saved.content)
        assertEquals(5_000L, saved.createdAt)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test fun `edit memuat data lama dan mempertahankan createdAt dan isPinned`() = runTest {
        val existing = NoteEntity(id = 3, title = "Lama", content = "isi lama",
            createdAt = 1_000L, isPinned = true)
        val repo = FakeNoteRepository(listOf(existing))
        val vm = NoteEditViewModel(repo, noteId = 3, clock = { 9_000L })
        runCurrent()

        assertEquals("Lama", vm.uiState.value.title)
        assertFalse(vm.uiState.value.isLoading)

        vm.onTitleChange("Baru")
        vm.save(); runCurrent()

        val saved = repo.current.single()
        assertEquals(3, saved.id)
        assertEquals("Baru", saved.title)
        assertEquals("createdAt tidak boleh tereset", 1_000L, saved.createdAt)
        assertTrue("isPinned tidak boleh tereset", saved.isPinned)
    }

    @Test fun `catatan yang tidak ditemukan menampilkan pesan`() = runTest {
        val vm = NoteEditViewModel(FakeNoteRepository(), noteId = 99)
        runCurrent()
        assertNotNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
    }
}
