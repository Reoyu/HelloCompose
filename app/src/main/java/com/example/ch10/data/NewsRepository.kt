package com.example.ch10.data

import javax.inject.Inject

import com.example.ch10.data.local.ArticleDao
import com.example.ch10.data.local.ArticleEntity
import com.example.ch10.data.mapper.toDomain
import com.example.ch10.data.mapper.toEntity
import com.example.ch10.data.model.Article
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import com.example.ch10.data.model.OfflineInfo
import com.example.ch10.data.remote.NewsApiService
import com.example.ch10.data.remote.dto.NewsResponseDto
import com.example.ch10.util.Clock
import kotlin.coroutines.cancellation.CancellationException

// Kunci cache untuk hasil pencarian terakhir (bukan nama kategori NewsAPI)
const val SEARCH_CACHE_KEY = "__search__"

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    // Untuk layar detail (Tugas 2): artikel apa pun yang pernah dimuat
    suspend fun getArticle(url: String): Article?
}

// Strategi (Tugas 4): selalu coba server dulu.
//  - Berhasil -> simpan ke Room, tampilkan data baru.
//  - Gagal    -> tampilkan cache Room + info "offline"; jika cache kosong, gagal.
//
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: Clock
) : NewsRepository {

    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> =
        fetch(
            cacheKey = category.apiValue,
            remote = { api.getTopHeadlines(category = category.apiValue) },
            cached = { dao.getArticles(category.apiValue) }
        )

    override suspend fun searchNews(query: String): Result<NewsFeed> =
        fetch(
            cacheKey = SEARCH_CACHE_KEY,
            remote = { api.searchNews(query = query) },
            cached = { dao.searchCached(query) }
        )

    override suspend fun getArticle(url: String): Article? =
        dao.getArticleByUrl(url)?.toDomain()

    private suspend fun fetch(
        cacheKey: String,
        remote: suspend () -> NewsResponseDto,
        cached: suspend () -> List<ArticleEntity>
    ): Result<NewsFeed> {
        val result = runCatching {
            remote().articles
                .mapNotNull { it.toDomain() }
                // NewsAPI bisa mengirim url ganda; LazyColumn(key = url) akan crash
                .distinctBy { it.url }
        }

        result.onSuccess { articles ->
            val now = clock.now()
            dao.replaceCategory(
                cacheKey,
                articles.mapIndexed { i, article -> article.toEntity(cacheKey, i, now) }
            )
            return Result.success(NewsFeed(articles))
        }

        val error = result.exceptionOrNull()!!
        // runCatching juga menangkap CancellationException (mis. pengguna sudah
        // pindah kategori). Itu BUKAN error jaringan: lempar ulang.
        if (error is CancellationException) throw error

        val offline = cached()
        if (offline.isEmpty()) return Result.failure(error)

        return Result.success(
            NewsFeed(
                articles = offline.map { it.toDomain() }.distinctBy { it.url },
                offline = OfflineInfo(
                    cachedAt = offline.maxOf { it.cachedAt },
                    reason = error.toUserMessage()
                )
            )
        )
    }
}
