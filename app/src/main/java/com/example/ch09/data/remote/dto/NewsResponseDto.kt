package com.example.ch09.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO = bentuk JSON dari server APA ADANYA. Field yang bisa null/hilang
// diberi nilai default agar parsing tidak gagal.
@Serializable
data class NewsResponseDto(
    val status: String,
    val totalResults: Int = 0,
    val articles: List<ArticleDto> = emptyList()
)

@Serializable
data class ArticleDto(
    val source: SourceDto? = null,
    // NewsAPI kadang mengirim title/url null untuk artikel yang dihapus
    val title: String? = null,
    val author: String? = null,
    val description: String? = null,
    val url: String? = null,
    @SerialName("urlToImage")
    val imageUrl: String? = null,
    val publishedAt: String? = null,
    val content: String? = null
)

@Serializable
data class SourceDto(
    val id: String? = null,
    val name: String? = null
)
