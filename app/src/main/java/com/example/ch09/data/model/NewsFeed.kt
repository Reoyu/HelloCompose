package com.example.ch09.data.model

// Hasil memuat berita. offline != null berarti data berasal dari cache Room
// karena permintaan ke server gagal. Di starter selalu null sampai Tugas 4.
data class NewsFeed(
    val articles: List<Article>,
    val offline: OfflineInfo? = null
)

data class OfflineInfo(
    val cachedAt: Long,          // kapan cache terakhir diperbarui
    val reason: String           // mengapa server gagal, mis. "Tidak ada koneksi internet"
)
