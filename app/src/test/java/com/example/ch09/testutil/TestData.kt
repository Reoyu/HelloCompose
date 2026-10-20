package com.example.ch09.testutil

import com.example.ch09.data.model.Article
import com.example.ch09.data.remote.dto.ArticleDto
import com.example.ch09.data.remote.dto.SourceDto

fun article(id: Int, title: String = "Berita $id") = Article(
    title = title,
    author = "Penulis",
    description = "Deskripsi $id",
    content = "",
    url = "https://contoh.com/$id",
    imageUrl = null,
    publishedAt = "2025-05-01T08:00:00Z",
    sourceName = "Sumber"
)

fun articleDto(id: Int, title: String? = "Berita $id", url: String? = "https://contoh.com/$id") =
    ArticleDto(
        source = SourceDto(name = "Sumber"),
        title = title,
        author = "Penulis",
        description = "Deskripsi $id",
        url = url,
        publishedAt = "2025-05-01T08:00:00Z"
    )
