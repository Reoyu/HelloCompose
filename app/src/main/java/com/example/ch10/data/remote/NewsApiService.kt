package com.example.ch10.data.remote

import com.example.ch10.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Negara untuk top-headlines. Ganti ke "id" untuk berita Indonesia; jika
// hasilnya kosong (totalResults = 0), kembalikan ke "us".
const val NEWS_COUNTRY = "us"

// Retrofit men-generate implementasi interface ini. API key TIDAK menjadi
// parameter: ditambahkan sebagai header oleh ApiKeyInterceptor (NetworkModule).
interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = NEWS_COUNTRY,
        @Query("category") category: String = "general",
        @Query("pageSize") pageSize: Int = 30
    ): NewsResponseDto

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 30
    ): NewsResponseDto
}
