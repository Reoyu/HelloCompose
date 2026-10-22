package com.example.ch10.ui.detail

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch10.ui.openUrl

@Composable
fun ArticleDetailRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: ArticleDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArticleDetailScreen(
        uiState = uiState,
        onReadMore = { url -> openUrl(context, url) },
        onNavigateBack = onNavigateBack
    )
}
