package com.example.ch10.testutil

import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.Article
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import kotlinx.coroutines.delay

// Pengganti repository sungguhan: tanpa jaringan dan tanpa Room.
// Setiap panggilan dicatat agar uji bisa memastikan API TIDAK dipanggil berlebihan.
class FakeNewsRepository : NewsRepository {

    var headlines: (NewsCategory) -> Result<NewsFeed> = { Result.success(NewsFeed(emptyList())) }
    var search: (String) -> Result<NewsFeed> = { Result.success(NewsFeed(emptyList())) }
    // Waktu (virtual) yang dibutuhkan "server" untuk menjawab
    var latencyMs: Long = 0L
    var stored: Map<String, Article> = emptyMap()

    val calls = mutableListOf<String>()

    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> {
        calls += "headlines:${category.apiValue}"
        delay(latencyMs)
        return headlines(category)
    }

    override suspend fun searchNews(query: String): Result<NewsFeed> {
        calls += "search:$query"
        delay(latencyMs)
        return search(query)
    }

    override suspend fun getArticle(url: String): Article? = stored[url]
}
