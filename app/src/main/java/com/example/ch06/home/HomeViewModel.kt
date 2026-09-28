package com.example.ch06.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch06.data.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allArticles: List<com.example.ch06.data.Article> = emptyList()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { repository.getArticles() }
                .onSuccess { articles ->
                    allArticles = articles

                    val currentQuery = _uiState.value.query

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            articles = filter(allArticles, currentQuery)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat artikel"
                        )
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update {
            it.copy(
                query = newQuery,
                articles = filter(allArticles, newQuery)
            )
        }
    }

    private fun filter(
        source: List<com.example.ch06.data.Article>,
        query: String
    ): List<com.example.ch06.data.Article> {
        if (query.isBlank()) {
            return source
        }

        return source.filter { article ->
            article.title.contains(query, ignoreCase = true) ||
                    article.category.contains(query, ignoreCase = true)
        }
    }
}