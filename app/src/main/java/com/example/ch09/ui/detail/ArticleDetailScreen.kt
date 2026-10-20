package com.example.ch09.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch09.ui.components.ArticleImage
import com.example.ch09.ui.components.articleMeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onNavigateBack: () -> Unit,
    onReadMore: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Detail Artikel") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
            }
        )
    }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            val article = uiState.article
            when {
                uiState.isLoading -> CircularProgressIndicator()
                article == null -> Text(uiState.errorMessage ?: "Artikel tidak ditemukan", Modifier.padding(24.dp))
                else -> Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ArticleImage(article.imageUrl, article.title, Modifier.fillMaxWidth().height(220.dp))
                    Text(articleMeta(article, System.currentTimeMillis()), style = MaterialTheme.typography.labelMedium)
                    Text(article.title, style = MaterialTheme.typography.headlineSmall)
                    Text("Oleh ${article.author}", style = MaterialTheme.typography.labelLarge)
                    if (article.description.isNotBlank()) Text(article.description, style = MaterialTheme.typography.bodyLarge)
                    if (article.content.isNotBlank()) Text(article.content, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onReadMore, modifier = Modifier.fillMaxWidth()) {
                        Text("Baca Selengkapnya")
                    }
                }
            }
        }
    }
}
