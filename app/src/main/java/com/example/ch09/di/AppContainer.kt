package com.example.ch09.di

import com.example.ch09.data.NewsRepository

// Manual DI (Chapter 6): satu tempat yang tahu cara merakit dependency.
interface AppContainer {
    val newsRepository: NewsRepository
}
