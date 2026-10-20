package com.example.ch09.testutil

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity

class FakeArticleDao : ArticleDao() {
    private val rows = mutableMapOf<Pair<String, String>, ArticleEntity>()
    override suspend fun getArticles(category: String) = rows.values
        .filter { it.category == category }.sortedBy { it.position }
    override suspend fun getArticleByUrl(url: String) = rows.values
        .filter { it.url == url }.maxByOrNull { it.cachedAt }
    override suspend fun searchCached(query: String) = rows.values
        .filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        .sortedWith(compareByDescending<ArticleEntity> { it.cachedAt }.thenBy { it.position })
    override suspend fun insertAll(articles: List<ArticleEntity>) {
        articles.forEach { rows[it.category to it.url] = it }
    }
    override suspend fun deleteCategory(category: String) {
        rows.keys.removeAll { it.first == category }
    }
}
