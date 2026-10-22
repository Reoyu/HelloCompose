package com.example.ch10.data

import com.example.ch10.data.model.NewsCategory
import com.example.ch10.testutil.FakeArticleDao
import com.example.ch10.testutil.FakeNewsApiService
import com.example.ch10.testutil.articleDto
import com.example.ch10.util.Clock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException

class NewsRepositoryImplTest {

    private val api = FakeNewsApiService()
    private val dao = FakeArticleDao()
    private var now = 1_000L
    private val repo = NewsRepositoryImpl(api, dao, clock = Clock { now })

    @Test fun `berhasil - artikel disimpan ke cache sesuai kategori dan urutan`() = runTest {
        api.articles = listOf(articleDto(2), articleDto(1))

        val feed = repo.getTopHeadlines(NewsCategory.Technology).getOrThrow()

        assertNull(feed.offline)
        assertEquals(listOf("https://contoh.com/2", "https://contoh.com/1"), feed.articles.map { it.url })
        val cached = dao.getArticles("technology")
        assertEquals(listOf(0, 1), cached.map { it.position })
        assertTrue(cached.all { it.cachedAt == 1_000L })
    }

    @Test fun `artikel removed, tanpa url, dan url ganda dibuang`() = runTest {
        api.articles = listOf(
            articleDto(1), articleDto(1), articleDto(2, title = "[Removed]"), articleDto(3, url = null)
        )
        val feed = repo.getTopHeadlines(NewsCategory.General).getOrThrow()
        assertEquals(listOf("https://contoh.com/1"), feed.articles.map { it.url })
    }

    @Test fun `memuat ulang mengganti cache lama kategori tersebut`() = runTest {
        api.articles = listOf(articleDto(1), articleDto(2))
        repo.getTopHeadlines(NewsCategory.General)
        api.articles = listOf(articleDto(3))
        repo.getTopHeadlines(NewsCategory.General)

        assertEquals(listOf("https://contoh.com/3"), dao.getArticles("general").map { it.url })
    }

    @Test fun `gagal dengan cache - tampil data offline beserta alasannya`() = runTest {
        api.articles = listOf(articleDto(1))
        repo.getTopHeadlines(NewsCategory.General)

        now = 9_000L
        api.error = IOException("offline")
        val feed = repo.getTopHeadlines(NewsCategory.General).getOrThrow()

        assertEquals(listOf("https://contoh.com/1"), feed.articles.map { it.url })
        val offline = feed.offline!!
        assertEquals("waktu cache, bukan waktu sekarang", 1_000L, offline.cachedAt)
        assertEquals("Tidak ada koneksi internet.", offline.reason)
    }

    @Test fun `gagal tanpa cache - Result failure`() = runTest {
        api.error = IOException()
        val result = repo.getTopHeadlines(NewsCategory.Health)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test fun `pencarian offline mencari di seluruh cache`() = runTest {
        api.articles = listOf(articleDto(1, title = "Harga Kopi Naik"), articleDto(2, title = "Bola"))
        repo.getTopHeadlines(NewsCategory.Business)

        api.error = IOException()
        val feed = repo.searchNews("kopi").getOrThrow()

        assertEquals(listOf("Harga Kopi Naik"), feed.articles.map { it.title })
        assertNotNull(feed.offline)
    }

    @Test fun `pembatalan coroutine tidak dianggap error jaringan`() = runTest {
        api.articles = listOf(articleDto(1))
        repo.getTopHeadlines(NewsCategory.General)       // ada cache

        api.error = CancellationException("pindah kategori")
        try {
            repo.getTopHeadlines(NewsCategory.General)
            fail("CancellationException harus dilempar ulang")
        } catch (e: CancellationException) {
            // benar: bukan diubah menjadi data offline
        }
    }

    @Test fun `artikel dari hasil pencarian bisa dibuka di layar detail`() = runTest {
        api.articles = listOf(articleDto(4))
        repo.searchNews("apa saja")

        assertEquals("Berita 4", repo.getArticle("https://contoh.com/4")?.title)
        assertNull(repo.getArticle("https://contoh.com/tidak-ada"))
    }

}
