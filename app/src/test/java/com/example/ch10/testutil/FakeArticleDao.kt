package com.example.ch10.testutil

import com.example.ch10.data.local.ArticleDao
import com.example.ch10.data.local.ArticleEntity

// Pengganti Room: menyimpan baris di memori (perilaku query disederhanakan)
class FakeArticleDao : ArticleDao() {

    val rows = mutableListOf<ArticleEntity>()

    override suspend fun getArticles(category: String) =
        rows.filter { it.category == category }.sortedBy { it.position }

    override suspend fun getArticleByUrl(url: String) =
        rows.filter { it.url == url }.maxByOrNull { it.cachedAt }

    override suspend fun searchCached(query: String) = rows.filter {
        it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
    }

    override suspend fun insertAll(articles: List<ArticleEntity>) {
        articles.forEach { new ->
            rows.removeAll { it.category == new.category && it.url == new.url }
            rows += new
        }
    }

    override suspend fun deleteCategory(category: String) {
        rows.removeAll { it.category == category }
    }
}
