package com.example.ch09.ui.news

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch09.MainApplication
import com.example.ch09.data.model.Article

// Route: satu-satunya bagian yang tahu soal ViewModel dan Context
@Composable
fun NewsRoute(
    onArticleClick: (Article) -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication

    val viewModel: NewsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { NewsViewModel(app.container.newsRepository) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewsScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onArticleClick = onArticleClick,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry
    )
}
