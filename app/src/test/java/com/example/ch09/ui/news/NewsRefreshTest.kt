package com.example.ch09.ui.news

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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Tugas 3: merah sampai TODO [T3.1] selesai
@OptIn(ExperimentalCoroutinesApi::class)
class NewsRefreshTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private fun success(vararg ids: Int) = Result.success(NewsFeed(ids.map { article(it) }))

    @Test fun `pull-to-refresh mempertahankan daftar selama memuat`() = runTest {
        val repo = FakeNewsRepository().apply { headlines = { success(1) } }
        val vm = NewsViewModel(repo)
        runCurrent()

        repo.latencyMs = 1_000
        repo.headlines = { success(1, 2) }
        vm.refresh(); runCurrent()

        assertTrue(vm.uiState.value.isRefreshing)
        assertFalse("bukan spinner layar penuh", vm.uiState.value.isLoading)
        assertEquals(1, vm.uiState.value.articles.size)

        advanceTimeBy(1_001); runCurrent()
        assertFalse(vm.uiState.value.isRefreshing)
        assertEquals(2, vm.uiState.value.articles.size)
    }
}
