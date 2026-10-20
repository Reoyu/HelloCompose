package com.example.ch09.data

import com.example.ch09.data.model.NewsCategory
import com.example.ch09.testutil.FakeArticleDao
import com.example.ch09.testutil.FakeNewsApiService
import com.example.ch09.testutil.articleDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImplTest {
    private val api = FakeNewsApiService()
    private val dao = FakeArticleDao()
    private val repository = NewsRepositoryImpl(api, dao) { 1234L }

    @Test fun `online menyimpan urutan dan membuang duplikat`() = runTest {
        api.articles = listOf(articleDto(2), articleDto(1), articleDto(2), articleDto(3, title = null))
        val feed = repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        assertNull(feed.offline)
        assertEquals(listOf("Berita 2", "Berita 1"), feed.articles.map { it.title })
        assertEquals(listOf(0, 1), dao.getArticles("general").map { it.position })
        assertEquals("Berita 2", repository.getArticle("https://contoh.com/2")?.title)
    }

    @Test fun `gagal jaringan membaca cache dan waktu simpan`() = runTest {
        api.articles = listOf(articleDto(1))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        api.error = IOException("offline")
        val feed = repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        assertEquals(1, feed.articles.size)
        assertEquals(1234L, feed.offline?.cachedAt)
        assertTrue(feed.offline!!.reason.isNotBlank())
        assertTrue(repository.getTopHeadlines(NewsCategory.Health).isFailure)
    }

    @Test fun `cache kosong tetap failure`() = runTest {
        val error = IOException("offline")
        api.error = error
        assertSame(error, repository.getTopHeadlines(NewsCategory.General).exceptionOrNull())
    }

    @Test fun `replace kategori tidak menghapus kategori lainnya`() = runTest {
        api.articles = listOf(articleDto(1))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        repository.getTopHeadlines(NewsCategory.Health).getOrThrow()
        api.articles = listOf(articleDto(2))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        assertEquals(listOf("Berita 2"), dao.getArticles("general").map { it.title })
        assertEquals(listOf("Berita 1"), dao.getArticles("health").map { it.title })
    }

    @Test fun `hasil kosong online menghapus cache kategori lama`() = runTest {
        api.articles = listOf(articleDto(1))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        api.articles = emptyList()
        assertTrue(repository.getTopHeadlines(NewsCategory.General).getOrThrow().articles.isEmpty())
        assertTrue(dao.getArticles("general").isEmpty())
    }

    @Test fun `pencarian tersimpan dan detail ditemukan dari repository baru`() = runTest {
        api.articles = listOf(articleDto(7))
        repository.searchNews("Berita").getOrThrow()
        assertEquals(1, dao.getArticles("__search__").size)
        val reopened = NewsRepositoryImpl(api, dao)
        assertEquals("Berita 7", reopened.getArticle("https://contoh.com/7")?.title)
        assertNull(reopened.getArticle("https://contoh.com/missing"))
    }

    @Test fun `pencarian offline lintas kategori tanpa URL ganda`() = runTest {
        api.articles = listOf(articleDto(1))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        repository.getTopHeadlines(NewsCategory.Health).getOrThrow()
        api.articles = listOf(articleDto(2))
        repository.searchNews("Berita").getOrThrow()
        api.error = IOException()
        val feed = repository.searchNews("BERITA").getOrThrow()
        assertEquals(2, feed.articles.size)
        assertNotNull(feed.offline)
        assertEquals(1, repository.searchNews("Deskripsi 1").getOrThrow().articles.size)
        assertTrue(repository.searchNews("tidak cocok").isFailure)
    }

    @Test fun `pembatalan dilempar ulang meskipun cache tersedia`() = runTest {
        api.articles = listOf(articleDto(1))
        repository.getTopHeadlines(NewsCategory.General).getOrThrow()
        val cancellation = CancellationException("cancel")
        api.error = cancellation
        try {
            repository.getTopHeadlines(NewsCategory.General)
            fail("CancellationException harus dilempar ulang")
        } catch (error: CancellationException) {
            assertSame(cancellation, error)
        }
    }
}
