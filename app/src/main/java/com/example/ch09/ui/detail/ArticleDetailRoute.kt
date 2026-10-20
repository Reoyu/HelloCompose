package com.example.ch09.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch09.MainApplication
import com.example.ch09.ui.openUrl

@Composable
fun ArticleDetailRoute(articleUrl: String, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as MainApplication
    val viewModel: ArticleDetailViewModel = viewModel(
        key = articleUrl,
        factory = viewModelFactory {
            initializer { ArticleDetailViewModel(app.container.newsRepository, articleUrl) }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArticleDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onReadMore = { openUrl(context, articleUrl) }
    )
}
