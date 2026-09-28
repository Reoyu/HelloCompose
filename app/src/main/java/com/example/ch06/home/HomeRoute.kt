package com.example.ch06.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch06.MainApplication

// Route: satu-satunya bagian yang tahu soal ViewModel dan Android framework
@Composable
fun HomeRoute(
    onArticleClick: (Int) -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(app.container.articleRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onArticleClick = onArticleClick,
        onQueryChange = viewModel::onQueryChange,
        onRetry = viewModel::refresh
    )
}
