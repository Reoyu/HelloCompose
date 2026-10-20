package com.example.ch09.testutil

import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.ArticleDto
import com.example.ch09.data.remote.dto.NewsResponseDto

// "Server" palsu untuk menguji NewsRepositoryImpl. error != null = request gagal.
class FakeNewsApiService : NewsApiService {

    var articles: List<ArticleDto> = emptyList()
    var error: Throwable? = null

    private fun respond(): NewsResponseDto {
        error?.let { throw it }
        return NewsResponseDto(status = "ok", totalResults = articles.size, articles = articles)
    }

    override suspend fun getTopHeadlines(country: String, category: String, pageSize: Int) = respond()

    override suspend fun searchNews(query: String, sortBy: String, pageSize: Int) = respond()
}
