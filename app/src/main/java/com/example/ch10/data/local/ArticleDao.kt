package com.example.ch10.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

// Abstract class (bukan interface) agar bisa punya fungsi @Transaction
// yang berisi implementasi.
@Dao
abstract class ArticleDao {

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY position")
    abstract suspend fun getArticles(category: String): List<ArticleEntity>

    // Untuk layar detail: cukup satu baris, yang paling baru di-cache
    @Query("SELECT * FROM articles WHERE url = :url ORDER BY cachedAt DESC LIMIT 1")
    abstract suspend fun getArticleByUrl(url: String): ArticleEntity?

    // Pencarian offline di seluruh cache (judul atau deskripsi)
    @Query(
        "SELECT * FROM articles " +
            "WHERE title LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' " +
            "ORDER BY publishedAt DESC"
    )
    abstract suspend fun searchCached(query: String): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE category = :category")
    abstract suspend fun deleteCategory(category: String)

    // Hapus cache lama lalu isi yang baru dalam SATU transaksi: tidak pernah
    // ada saat di mana kategori itu kosong separuh jalan.
    @Transaction
    open suspend fun replaceCategory(category: String, articles: List<ArticleEntity>) {
        deleteCategory(category)
        insertAll(articles)
    }
}
