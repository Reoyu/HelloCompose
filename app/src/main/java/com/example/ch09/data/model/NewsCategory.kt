package com.example.ch09.data.model

// Kategori yang didukung endpoint top-headlines NewsAPI
enum class NewsCategory(val apiValue: String, val label: String) {
    General("general", "Umum"),
    Technology("technology", "Teknologi"),
    Business("business", "Bisnis"),
    Health("health", "Kesehatan"),
    Science("science", "Sains"),
    Sports("sports", "Olahraga"),
    Entertainment("entertainment", "Hiburan")
}
