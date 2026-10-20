package com.example.ch09.data.local

import androidx.room.Entity

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
