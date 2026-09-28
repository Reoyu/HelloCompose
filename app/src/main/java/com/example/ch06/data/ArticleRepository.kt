package com.example.ch06.data

import kotlinx.coroutines.delay

// Abstraksi sumber data — ViewModel tidak perlu tahu datanya dari mana
interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    fun getCachedArticles(): List<Article>
}

// Implementasi palsu untuk latihan; nanti bisa diganti Retrofit/Room
class FakeArticleRepository : ArticleRepository {
    private val cached = listOf(
        Article(1, "Compose Basics", "UI", "Memahami layout dasar dan konsep deklaratif Jetpack Compose."),
        Article(2, "StateFlow di Android", "Arsitektur", "Mengelola state reaktif dengan StateFlow dan ViewModel."),
        Article(3, "Navigation Compose", "Navigasi", "Membangun aplikasi multi-layar dengan NavController."),
        Article(4, "Room untuk Pemula", "Database", "Menyimpan data lokal secara persisten dengan Room."),
        Article(5, "Retrofit & Networking", "Jaringan", "Mengambil data dari REST API menggunakan Retrofit.")
    )

    override suspend fun getArticles(): List<Article> {
        delay(1200) // simulasi latensi jaringan
        return cached
    }

    override fun getCachedArticles(): List<Article> = cached
}
