package com.example.ch09.data

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    suspend fun getArticle(url: String): Article?
}

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {
    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> = fetch(
        cacheKey = category.apiValue,
        cached = { dao.getArticles(category.apiValue) },
        remote = { api.getTopHeadlines(category = category.apiValue) }
    )

    override suspend fun searchNews(query: String): Result<NewsFeed> = fetch(
        cacheKey = "__search__",
        cached = { dao.searchCached(query.trim()) },
        remote = { api.searchNews(query = query.trim()) }
    )

    override suspend fun getArticle(url: String): Article? = dao.getArticleByUrl(url)?.toDomain()

    private suspend fun fetch(
        cacheKey: String,
        cached: suspend () -> List<ArticleEntity>,
        remote: suspend () -> NewsResponseDto
    ): Result<NewsFeed> {
        val result = try {
            Result.success(remote().articles.mapNotNull { it.toDomain() }.distinctBy { it.url })
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
        return try {
            val error = result.exceptionOrNull()
            if (error == null) {
                val articles = result.getOrThrow()
                val now = clock()
                dao.replaceCategory(cacheKey, articles.mapIndexed { index, article ->
                    article.toEntity(cacheKey, index, now)
                })
                Result.success(NewsFeed(articles))
            } else {
                val entries = cached().distinctBy { it.url }
                if (entries.isEmpty()) Result.failure(error)
                else Result.success(NewsFeed(
                    articles = entries.map { it.toDomain() },
                    offline = OfflineInfo(entries.minOf { it.cachedAt }, error.toUserMessage())
                ))
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
