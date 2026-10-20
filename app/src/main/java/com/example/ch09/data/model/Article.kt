package com.example.ch09.data.model

// Domain model: dipakai ViewModel dan UI. Bebas dari anotasi serialization
// maupun Room, dan semua field sudah "bersih" (tidak ada null yang tak perlu).
data class Article(
    val title: String,
    val author: String,
    val description: String,
    val content: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,     // ISO-8601, mis. "2025-05-01T08:00:00Z"
    val sourceName: String
)
