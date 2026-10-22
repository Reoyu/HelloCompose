package com.example.ch10.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.ch10.ARG_ARTICLE_URL
import com.example.ch10.testutil.FakeNewsRepository
import com.example.ch10.testutil.MainDispatcherRule
import com.example.ch10.testutil.article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    // Meniru Navigation: argumen route masuk ke SavedStateHandle
    private fun args(url: String) = SavedStateHandle(mapOf(ARG_ARTICLE_URL to url))

    @Test fun `memuat artikel berdasarkan url`() = runTest {
        val target = article(5)
        val repo = FakeNewsRepository().apply { stored = mapOf(target.url to target) }
        val vm = ArticleDetailViewModel(repo, args(target.url))
        runCurrent()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(target, vm.uiState.value.article)
    }

    @Test fun `url tidak dikenal menampilkan pesan`() = runTest {
        val vm = ArticleDetailViewModel(FakeNewsRepository(), args("https://tidak.ada"))
        runCurrent()

        assertNull(vm.uiState.value.article)
        assertEquals("Artikel tidak ditemukan", vm.uiState.value.errorMessage)
    }
}
