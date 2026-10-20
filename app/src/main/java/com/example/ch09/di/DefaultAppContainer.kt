package com.example.ch09.di

import android.content.Context
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.NewsRepositoryImpl
import com.example.ch09.data.local.NewsDatabase
import com.example.ch09.data.remote.NetworkModule

class DefaultAppContainer(context: Context) : AppContainer {
    private val database by lazy { NewsDatabase.getInstance(context.applicationContext) }
    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(api = NetworkModule.createNewsApiService(), dao = database.articleDao())
    }
}
