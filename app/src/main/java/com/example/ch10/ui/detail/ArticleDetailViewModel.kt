package com.example.ch10.ui.detail

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch10.ARG_ARTICLE_URL
import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)

// Tugas 2: layar detail. Yang dikirim lewat navigasi hanya URL artikel;
// datanya diambil dari repository (cache Room), bukan objek Article utuh.
//
// articleUrl dibaca dari SavedStateHandle: Navigation otomatis memasukkan
// argumen route ke sana. Hilt juga meng-inject SavedStateHandle ke setiap
// @HiltViewModel, jadi constructor ini tidak perlu diubah.
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val repository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleUrl: String = savedStateHandle.get<String>(ARG_ARTICLE_URL).orEmpty()

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val article = repository.getArticle(articleUrl)
            _uiState.update {
                if (article == null) {
                    it.copy(isLoading = false, errorMessage = "Artikel tidak ditemukan")
                } else {
                    it.copy(isLoading = false, article = article)
                }
            }
        }
    }
}
