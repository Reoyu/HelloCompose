package com.example.ch10.data.local

import androidx.room.Entity

// Cache artikel per "category" (nama kategori, atau SEARCH_CACHE_KEY untuk
// hasil pencarian terakhir). Artikel yang sama bisa muncul di beberapa
// kategori, karena itu primary key-nya gabungan (category, url).
@Entity(tableName = "articles", primaryKeys = ["category", "url"])
data class ArticleEntity(
    val category: String,
    val url: String,
    val title: String,
    val author: String,
    val description: String,
    val content: String,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val position: Int,
    val cachedAt: Long
)
