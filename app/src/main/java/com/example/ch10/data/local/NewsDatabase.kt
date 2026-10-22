package com.example.ch10.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true          // JSON skema -> app/schemas/ (lihat build.gradle.kts)
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        const val NAME = "news_database"

    }
}
