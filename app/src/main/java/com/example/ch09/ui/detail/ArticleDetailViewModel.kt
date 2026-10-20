package com.example.ch09.ui.detail

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.example.ch09.data.toUserMessage
import androidx.lifecycle.ViewModel
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)

// Tugas 2: layar detail. Yang dikirim lewat navigasi hanya URL artikel;
// datanya diambil dari repository, bukan objek Article utuh.
class ArticleDetailViewModel(
    private val repository: NewsRepository,
    private val articleUrl: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = try {
                val article = repository.getArticle(articleUrl)
                ArticleDetailUiState(
                    isLoading = false,
                    article = article,
                    errorMessage = if (article == null) "Artikel tidak ditemukan" else null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                ArticleDetailUiState(isLoading = false, errorMessage = error.toUserMessage())
            }
        }
    }
}
