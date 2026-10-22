package com.example.ch10.data.mapper

import com.example.ch10.data.local.ArticleEntity
import com.example.ch10.data.model.Article
import com.example.ch10.data.remote.dto.ArticleDto

// NewsAPI menandai artikel yang sudah dihapus penerbitnya dengan "[Removed]"
private const val REMOVED = "[Removed]"

// Potongan "… [+1234 chars]" di akhir content (batas paket gratis NewsAPI)
private val TRUNCATION_SUFFIX = Regex("""\s*\[\+\d+ chars]$""")

// DTO -> domain. Mengembalikan null untuk artikel yang tidak layak tampil
// (tanpa judul/url, atau sudah dihapus), sehingga dipakai dengan mapNotNull.
fun ArticleDto.toDomain(): Article? {
    val title = title?.trim().orEmpty()
    val url = url?.trim().orEmpty()
    if (title.isEmpty() || url.isEmpty() || title == REMOVED) return null

    return Article(
        title = title,
        author = author?.takeIf { it.isNotBlank() } ?: "Anonim",
        description = description?.trim().orEmpty(),
        content = content.orEmpty().replace(TRUNCATION_SUFFIX, "").trim(),
        url = url,
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt.orEmpty(),
        sourceName = source?.name?.takeIf { it.isNotBlank() } ?: "Tanpa sumber"
    )
}

// Domain <-> Room (Tugas 4). position menjaga urutan seperti dari server.
fun Article.toEntity(category: String, position: Int, cachedAt: Long) = ArticleEntity(
    category = category,
    url = url,
    title = title,
    author = author,
    description = description,
    content = content,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    sourceName = sourceName,
    position = position,
    cachedAt = cachedAt
)

fun ArticleEntity.toDomain() = Article(
    title = title,
    author = author,
    description = description,
    content = content,
    url = url,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    sourceName = sourceName
)
