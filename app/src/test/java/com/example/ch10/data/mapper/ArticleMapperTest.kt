package com.example.ch10.data.mapper

import com.example.ch10.data.remote.dto.ArticleDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleMapperTest {

    @Test fun `field null diganti nilai yang layak tampil`() {
        val article = ArticleDto(title = " Judul ", url = "https://a.com", author = "  ").toDomain()!!

        assertEquals("Judul", article.title)
        assertEquals("Anonim", article.author)
        assertEquals("", article.description)
        assertEquals("Tanpa sumber", article.sourceName)
        assertNull(article.imageUrl)
    }

    @Test fun `artikel tanpa judul, tanpa url, atau Removed dibuang`() {
        assertNull(ArticleDto(title = null, url = "https://a.com").toDomain())
        assertNull(ArticleDto(title = "A", url = " ").toDomain())
        assertNull(ArticleDto(title = "[Removed]", url = "https://removed.com").toDomain())
    }

    @Test fun `penanda potongan content dihapus`() {
        val article = ArticleDto(
            title = "A", url = "https://a.com",
            content = "Kalimat pembuka berita… [+2345 chars]"
        ).toDomain()!!
        assertEquals("Kalimat pembuka berita…", article.content)
    }

    @Test fun `domain ke entity dan kembali tidak mengubah data`() {
        val article = ArticleDto(title = "A", url = "https://a.com", imageUrl = "https://a.com/i.png").toDomain()!!
        val entity = article.toEntity(category = "general", position = 3, cachedAt = 99L)

        assertEquals("general", entity.category)
        assertEquals(3, entity.position)
        assertEquals(article, entity.toDomain())
    }
}
