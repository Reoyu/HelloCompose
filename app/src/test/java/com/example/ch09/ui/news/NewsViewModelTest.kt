package com.example.ch09.ui.news

import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.testutil.FakeNewsRepository
import com.example.ch09.testutil.MainDispatcherRule
import com.example.ch09.testutil.article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private fun success(vararg ids: Int) = Result.success(NewsFeed(ids.map { article(it) }))

    @Test fun `headline dimuat saat dibuka`() = runTest {
        val repo = FakeNewsRepository().apply { headlines = { success(1, 2) } }
        val vm = NewsViewModel(repo)
        runCurrent()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.articles.size)
        assertEquals(listOf("headlines:general"), repo.calls)
    }

    @Test fun `gagal tanpa cache menampilkan pesan error`() = runTest {
        val repo = FakeNewsRepository().apply { headlines = { Result.failure(IOException()) } }
        val vm = NewsViewModel(repo)
        runCurrent()

        assertEquals("Tidak ada koneksi internet.", vm.uiState.value.errorMessage)
        assertTrue(vm.uiState.value.articles.isEmpty())

        repo.headlines = { success(1) }
        vm.retry(); runCurrent()
        assertNull(vm.uiState.value.errorMessage)
        assertEquals(1, vm.uiState.value.articles.size)
    }

    @Test fun `ganti kategori memuat ulang, kategori yang sama diabaikan`() = runTest {
        val repo = FakeNewsRepository().apply { headlines = { success(1) } }
        val vm = NewsViewModel(repo)
        runCurrent()

        vm.onCategorySelected(NewsCategory.Technology); runCurrent()
        vm.onCategorySelected(NewsCategory.Technology); runCurrent()

        assertEquals(NewsCategory.Technology, vm.uiState.value.selectedCategory)
        assertEquals(listOf("headlines:general", "headlines:technology"), repo.calls)
    }

    @Test fun `pencarian menunggu pengguna berhenti mengetik`() = runTest {
        val repo = FakeNewsRepository().apply {
            headlines = { success(1, 2, 3) }
            search = { success(9) }
        }
        val vm = NewsViewModel(repo, searchDebounceMs = 500)
        runCurrent()

        vm.onQueryChange("a"); advanceTimeBy(200)
        vm.onQueryChange("ai"); advanceTimeBy(200)
        vm.onQueryChange("ai news"); runCurrent()
        assertEquals("teks langsung tampil", "ai news", vm.uiState.value.query)
        assertEquals("belum ada request pencarian", 1, repo.calls.size)

        advanceTimeBy(501); runCurrent()
        assertEquals("hanya SATU request", listOf("headlines:general", "search:ai news"), repo.calls)
        assertEquals(listOf("https://contoh.com/9"), vm.uiState.value.articles.map { it.url })
    }

    @Test fun `menghapus pencarian langsung kembali ke headline`() = runTest {
        val repo = FakeNewsRepository().apply {
            headlines = { success(1, 2) }
            search = { success(9) }
        }
        val vm = NewsViewModel(repo)
        runCurrent()
        vm.onQueryChange("ai"); advanceTimeBy(501); runCurrent()

        vm.onQueryChange(""); runCurrent()

        assertFalse(vm.uiState.value.isSearching)
        assertEquals(2, vm.uiState.value.articles.size)
        assertEquals("headlines:general", repo.calls.last())
    }

    @Test fun `hasil lama tidak menimpa hasil kategori terbaru`() = runTest {
        val repo = FakeNewsRepository().apply {
            latencyMs = 1_000
            headlines = { category ->
                if (category == NewsCategory.Sports) success(7) else success(1)
            }
        }
        val vm = NewsViewModel(repo)
        runCurrent()                                   // general: masih menunggu server
        vm.onCategorySelected(NewsCategory.Sports)     // membatalkan request general
        advanceTimeBy(1_001); runCurrent()

        assertEquals(listOf("https://contoh.com/7"), vm.uiState.value.articles.map { it.url })
        assertFalse(vm.uiState.value.isLoading)
    }
}
