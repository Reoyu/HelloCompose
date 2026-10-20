package com.example.ch09.ui.detail

import com.example.ch09.testutil.FakeNewsRepository
import com.example.ch09.testutil.MainDispatcherRule
import com.example.ch09.testutil.article
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

    @Test fun `memuat artikel berdasarkan url`() = runTest {
        val target = article(5)
        val repo = FakeNewsRepository().apply { stored = mapOf(target.url to target) }
        val vm = ArticleDetailViewModel(repo, target.url)
        runCurrent()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(target, vm.uiState.value.article)
    }

    @Test fun `url tidak dikenal menampilkan pesan`() = runTest {
        val vm = ArticleDetailViewModel(FakeNewsRepository(), "https://tidak.ada")
        runCurrent()

        assertNull(vm.uiState.value.article)
        assertEquals("Artikel tidak ditemukan", vm.uiState.value.errorMessage)
    }
}
