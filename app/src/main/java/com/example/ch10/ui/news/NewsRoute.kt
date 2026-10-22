package com.example.ch10.ui.news

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch10.data.model.Article

// Route: satu-satunya bagian yang tahu soal ViewModel dan Context
@Composable
fun NewsRoute(
    onArticleClick: (Article) -> Unit
) {
    val viewModel: NewsViewModel = hiltViewModel()
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
