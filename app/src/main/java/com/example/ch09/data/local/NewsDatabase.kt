package com.example.ch09.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = true)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile private var instance: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                "news.db"
            ).build().also { instance = it }
        }
    }
}
